package com.ruoyi.bc.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.bc.component.CatFeeApiClient;
import com.ruoyi.bc.component.ComponentBlockChainService;
import com.ruoyi.bc.service.IBlockChainApiService;
import com.ruoyi.blockchain.domain.ApiBcTransaction;
import com.ruoyi.blockchain.domain.BcApiKeys;
import com.ruoyi.blockchain.domain.TokenPrices;
import com.ruoyi.blockchain.domain.TronTransaction;
import com.ruoyi.blockchain.service.IBcApiKeysService;
import com.ruoyi.blockchain.service.ITokenPricesService;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tron.trident.abi.FunctionEncoder;
import org.tron.trident.abi.TypeReference;
import org.tron.trident.abi.datatypes.Address;
import org.tron.trident.abi.datatypes.Bool;
import org.tron.trident.abi.datatypes.Function;
import org.tron.trident.abi.datatypes.generated.Uint256;
import org.tron.trident.core.ApiWrapper;
import org.tron.trident.core.key.KeyPair;
import org.tron.trident.core.transaction.TransactionBuilder;
import org.tron.trident.proto.Chain;
import org.tron.trident.proto.Response;

import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BlockChainTronApiService implements IBlockChainApiService {

    private static final Logger log = LoggerFactory.getLogger(BlockChainTronApiService.class);

    private final ITokenPricesService tokenPriceService;
    private final IBcApiKeysService apiKeyService;


    private static final String CHAIN_TYPE = "TRX";
    // 单例实例（volatile 保证线程安全）
    private static volatile BlockChainTronApiService instance;

    // 构造方法私有化
    private BlockChainTronApiService(ITokenPricesService tokenPriceService, IBcApiKeysService apiKeyService) {
        this.tokenPriceService = tokenPriceService;
        this.apiKeyService = apiKeyService;
        //初始化币种配置
        tokenPriceService.initTokenConfigCache(CHAIN_TYPE);
    }

    /**
     * 获取单例实例（线程安全 + 依赖注入）
     */
    public static BlockChainTronApiService getInstance(ITokenPricesService tokenPriceService, IBcApiKeysService apiKeyService) {
        if (instance == null) {
            synchronized (BlockChainTronApiService.class) {
                if (instance == null) {
                    instance = new BlockChainTronApiService(tokenPriceService, apiKeyService);
                }
            }
        }
        return instance;
    }

    // 缓存 Web3j 实例，避免重复创建连接
    private final Map<String, ApiWrapper> apiWrapperCache = new ConcurrentHashMap<>();

    private final Map<ApiWrapper, Long> ApiToKeyId = new ConcurrentHashMap<>();

    /**
     * 获取一个可用的 ApiWrapper 实例（自动轮询多个 RPC）
     */
    public ApiWrapper getApiWrapper() {
        String chainType=CHAIN_TYPE;
        BcApiKeys apiKey = apiKeyService.getAvailableKey(chainType);
        if (apiKey == null || StringUtils.isEmpty(apiKey.getApiKey())) {

            throw new RuntimeException("无可用的 " + chainType + " 节点");
        }
        Exception lastEx = null;
        Long apiKeyId=apiKey.getId();
       // String url =apiKey.getApiUrl();
        String cacheKey = chainType + ":" + apiKeyId;
        try {
            // 从缓存获取或创建新的 Web3j 实例
            ApiWrapper apiWrapper = apiWrapperCache.computeIfAbsent(cacheKey, k -> {
                // Nile 测试网
                if (apiKey.getApiUrl().contains("nile")) {
                    return new ApiWrapper("grpc.nile.trongrid.io:50051","grpc.nile.trongrid.io:50061","");
                }
                // 主网（推荐使用 API Key）
                return ApiWrapper.ofMainnet("", apiKey.getApiKey());
            });
            // 绑定 apiKeyId，如果已有映射且不相同就更新
            ApiToKeyId.putIfAbsent(apiWrapper, apiKeyId);

            return apiWrapper;

        } catch (Exception e) {
            lastEx = e;

            // 标记节点失败（或限流）
            // 移除缓存，防止下次继续用坏节点
            this.invalidateNode(chainType,apiKeyId);

        }

        throw new RuntimeException("所有 Web3 节点都不可用", lastEx);
    }
    /**
     * 手动清理某个节点的缓存（例如检测到节点状态变化）
     */
    private void invalidateNode(String chainType, Long apiKeyId) {
        String cacheKey = chainType + ":" + apiKeyId;
        apiWrapperCache.remove(cacheKey);
    }

    /**
     * 更新接口调用的次数
     * @param apiWrapper
     */
    private void setApiCount(ApiWrapper apiWrapper){
        Long apiKeyId= ApiToKeyId.get(apiWrapper);
        if (apiKeyId != null) {
            apiKeyService.recordUsage(apiKeyId);
        }
    }

    @Override
    public Long getLatestBlockNumber() {
        try {
            String body = executeRequest("/wallet/getnowblock", null, true);
            JSONObject json = JSONObject.parseObject(body);
            JSONObject blockHeader = json.getJSONObject("block_header");
            JSONObject rawData = blockHeader.getJSONObject("raw_data");

            Long blockNumber = rawData.getLong("number");
            //log.debug("当前最新区块: {}", blockNumber);

            return blockNumber;

        } catch (Exception e) {
            log.error("获取 TRON 最新区块失败", e);
            return 0L;
        }
    }

    @Override
    public List<ApiBcTransaction> getBlockTransactions(Long startBlockNum, Long endBlockNum, ComponentBlockChainService handlerChainService) {

        List<ApiBcTransaction> transactionList = new ArrayList<>();
        //可以替换成获取单条的
        JSONArray txBlocks = this.getBlockByLimitNext(startBlockNum, endBlockNum);
        if (txBlocks == null || txBlocks.isEmpty()) {
            log.warn("区块 {} - {} 数据为空", startBlockNum, endBlockNum);
            return transactionList;
        }
        for (int i = 0; i < txBlocks.size(); i++) {
            JSONObject block = txBlocks.getJSONObject(i);
            long number = block.getJSONObject("block_header").getJSONObject("raw_data").getLong("number");

            List<TronTransaction> tronTransactions = parseBlockTransactions(block, number);
            if (tronTransactions.size() > 0) {
                transactionList.addAll(tronTransactions);
            }
        }
        //保存数据
        handlerChainService.HandlerSaveTransaction(transactionList, CHAIN_TYPE);
        return transactionList;
    }

    /**
     * 计算所需要的转账主币数量（含带宽）
     * @param gasPrice
     * @param gas
     * @return
     */
    public BigDecimal getFee(BigDecimal gasPrice,BigDecimal gas,boolean hasEnergy){
        try {
            if (hasEnergy){
                return new BigDecimal("65000");
            }
            BigDecimal gasLimit = gas.multiply(gasPrice).multiply(BigDecimal.TEN);
            //System.out.println("gasLimit="+gasLimit);
            BigDecimal feeTrx= gasLimit.divide(BigDecimal.TEN.pow(6)).setScale(5, RoundingMode.DOWN);
            return feeTrx;
        }catch (Exception e){
            return new BigDecimal("14");
        }
    }

    /**
     * 获取需要的能量
     * @param fromAddr
     * @param contractAddr
     * @param method
     * @return
     */
    public BigDecimal getGas(String fromAddr ,String contractAddr,String method){
       return getGasApi(fromAddr,contractAddr,method);
    }

    /**
     * 获取能量的价格
     * @return
     */
    public BigDecimal getGasPrice(){
        return new BigDecimal("420");
    }

    /**
     * 主币转账
     * @param fromAddr
     * @param fromPrv
     * @param toAddr
     * @param amount
     * @return 返回交易hash
     */
    public String transfer(String fromAddr,String fromPrv,String toAddr,BigDecimal amount,BigDecimal gasPrice,BigDecimal gas){
        String signedData= sendTrx(fromAddr,fromPrv,toAddr,amount);
        if(signedData!=null){
            return broadcastHex(signedData);
        }
        return null;
    }

    /**
     * 代币转账
     * @param fromAddr
     * @param fromPrv
     * @param contractAddr
     * @param toAddr
     * @param amount
     * @return
     */
    public String transferToken(String fromAddr,String fromPrv,String contractAddr,String toAddr,BigDecimal amount,BigDecimal gasPrice,BigDecimal gas){
        String signedData= sendErc20(fromAddr,fromPrv,contractAddr,toAddr,amount,gas);
        //transferTokenWithPayer()
        if(signedData!=null){
            return broadcastHex(signedData);
        }
        return null;
    }
    /**
     * 发送收续费
     * @param fromAddr
     * @param fromPrv
     * @param toAddr
     * @param amount
     * @param hasEnergy
     * @return 交易hash
     */
    public String sendTxFee(String fromAddr,String fromPrv,String toAddr,BigDecimal amount,BigDecimal gasPrice,BigDecimal gas,boolean hasEnergy){
        //发送能量
        if(hasEnergy){
            return sendEnergy(toAddr,amount.longValue());
        }
        return transfer(fromAddr,fromPrv,toAddr,amount,null,gas);
    }

    /**
     * 判断区块交易状态
     * @param txHash
     * @return
     */
    public Map<String, Object> checkTxStatus(String txHash){
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("blockNumber", null);
        result.put("gasUsed", 0);
        result.put("mainCoin", 0);
        if (txHash.contains("-")){
            result= checkOderStatus(txHash);
            return result;
        }

        JSONObject jsonObject=getTransactionInfoApi(txHash);
        if (jsonObject == null || jsonObject.isEmpty()) {
            return result;
        }
        if (!jsonObject.containsKey("receipt")){
            return result;
        }
        log.info("查询交易状态返回：{}",jsonObject);
        JSONObject receipt=jsonObject.getJSONObject("receipt");
        // 解析成功状态
        boolean success = "SUCCESS".equalsIgnoreCase(receipt.getString("result"));
        // 取得区块号
        BigInteger blockNumber = jsonObject.getBigInteger("blockNumber");
        // 取出此次交易消耗
        BigInteger energyUsed = receipt.getBigInteger("energy_usage_total");     // 交易消耗能量
        //BigInteger netUsed = receipt.getBigInteger("net_usage");                // 消耗带宽 (字节数)
       // BigInteger fee = receipt.getBigInteger("fee");                          // 直接扣的 SUN 手续费 (包含能量和带宽不足时的扣费)

        BigInteger energyCostSUN = energyUsed.multiply(getGasPrice().toBigInteger());
        BigDecimal mainCoin = new BigDecimal(energyCostSUN).divide(BigDecimal.TEN.pow(6), 6, RoundingMode.HALF_UP);


        result.put("success", success);
        result.put("blockNumber", blockNumber);
        result.put("gasUsed", energyUsed);
        result.put("mainCoin", mainCoin);

        return result;

    }



//===============私有的工具方法=====================

    /**
     * 调用第三方接口租赁能量
     * @param gas 能量暂时一笔65000，无usdt 131000
     * @param address
     * @return 返回第三方的id
     */
    private String sendEnergy(String address,Long gas){
        try {
            CatFeeApiClient.ApiResponse<CatFeeApiClient.OrderResult> res=null;
            res= CatFeeApiClient.me.createEnergyOrder(gas,address,"1h");
            if (res.isSuccess()){
                return res.getData().getId();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取第三方的状态
     * @param txId
     * @return
     */
    private Map<String, Object> checkOderStatus(String txId){
        boolean hasConfirm=false;
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("blockNumber", null);
        result.put("gasUsed", 0);
        result.put("mainCoin", 0);

        try {
            CatFeeApiClient.ApiResponse<CatFeeApiClient.OrderDetail> res=null;
            res= CatFeeApiClient.me.getOrderDetail(txId);
            if (!res.isSuccess()){
                result.put("success", false);
                return result;
            }
            if ("DELEGATE_SUCCESS".equals(res.getData().getStatus())){
                result.put("success", true);
            }

            if("DELEGATION_CONFIRMED".equals(res.getData().getConfirmStatus())){
                result.put("success", true);
                result.put("blockNumber", 20);
                result.put("gasUsed", res.getData().getDuration());
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result;
        }
    }
    /**
     * 解析区块中的交易
     */
    private List<TronTransaction> parseBlockTransactions(JSONObject block, Long blockNumber) {
        List<TronTransaction> transactions = new ArrayList<>();
        try {
            JSONArray txList = block.getJSONArray("transactions");
            if (txList == null || txList.isEmpty()) {
                return transactions;
            }

            for (int i = 0; i < txList.size(); i++) {
                JSONObject tx = txList.getJSONObject(i);

                TronTransaction parsedTx = parseTransaction(tx, blockNumber);
                if (parsedTx != null) {
                    transactions.add(parsedTx);
                }
            }

        } catch (Exception e) {
            log.error("解析区块 {} 交易失败", blockNumber, e);
        }

        return transactions;
    }

    /**
     * 解析单个交易
     */
    private TronTransaction parseTransaction(JSONObject tx, Long blockNumber) {
        try {
            String txId = tx.getString("txID");

            // 获取交易状态
            JSONArray ret = tx.getJSONArray("ret");
            if (ret == null || ret.isEmpty()) {
                return null;
            }
            String contractRet = ret.getJSONObject(0).getString("contractRet");
            boolean isSuccess = "SUCCESS".equals(contractRet);

            JSONObject rawData = tx.getJSONObject("raw_data");
            //交易时间
            Long timestamp = rawData.getLong("timestamp");
            // 获取合约列表
            JSONArray contractList = rawData.getJSONArray("contract");

            if (contractList == null || contractList.isEmpty()) {
                //log.debug("合约类型为空 : {} ", tx);
                return null;
            }
            JSONObject contract = contractList.getJSONObject(0);
            String contractType = contract.getString("type");
            JSONObject parameter = contract.getJSONObject("parameter");
            JSONObject value = parameter.getJSONObject("value");

            // 根据合约类型解析
            if ("TransferContract".equals(contractType)) {
                // TRX 主币转账
                return parseTrxTransfer(txId, blockNumber, value, isSuccess, timestamp);
            } else if ("TriggerSmartContract".equals(contractType)) {
                // 智能合约调用（可能是 TRC20 转账）
                return parseTrc20TransferFromData(txId, blockNumber, value, isSuccess, timestamp);
            }

        } catch (Exception e) {
            log.error("解析交易异常", e);
        }

        return null;
    }


    /**
     * 解析 TRX 主币转账
     */
    private TronTransaction parseTrxTransfer(String txId, Long blockNumber,
                                             JSONObject value, boolean isSuccess, Long timestamp) {
        try {
            // 获取主币配置
            TokenPrices trxConfig = tokenPriceService.getTokenCacheByChain(CHAIN_TYPE, CHAIN_TYPE);
            if (trxConfig == null || trxConfig.getEnabled() != 1) {
                return null;
            }

            String ownerAddress = value.getString("owner_address");
            String toAddress = value.getString("to_address");
            Long amount = value.getLong("amount");

            String fromAddr = hexAddressToBase58(ownerAddress);
            String toAddr = hexAddressToBase58(toAddress);

            BigDecimal amountTrx = new BigDecimal(amount)
                    .divide(BigDecimal.TEN.pow(trxConfig.getDecimals()), 18, RoundingMode.DOWN);

            TronTransaction transaction = new TronTransaction();
            transaction.setTxHash(txId);
            transaction.setBlockNumber(blockNumber);
            transaction.setFromAddress(fromAddr);
            transaction.setToAddress(toAddr);
            transaction.setAmount(amountTrx);
            transaction.setTokenSymbol(trxConfig.getTokenSymbol());
            transaction.setTokenDecimals(trxConfig.getDecimals());
            transaction.setTxType(CHAIN_TYPE);
            //交易状态 （1待完成，2已完成，3失败
            transaction.setStatus(isSuccess ? "2" : "3");
            transaction.setBlockTimestamp(timestamp);
            transaction.setContractResult(isSuccess);

            //log.debug("解析 TRX 转账: {} TRX from {} to {}", amountTrx, fromAddr, toAddr);

            return transaction;

        } catch (Exception e) {
            log.error("解析 TRX 转账失败", e);
            return null;
        }
    }

    /**
     * 直接从data字段解析TRC20转账
     */
    private TronTransaction parseTrc20TransferFromData(String txId, Long blockNumber,
                                                       JSONObject value, boolean isSuccess, Long timestamp) {
        try {
            String contractAddress = value.getString("contract_address");
            String contractBase58 = hexAddressToBase58(contractAddress);

            // 从数据库获取代币配置
            TokenPrices tokenConfig = tokenPriceService.getTokenCacheByContractAddress(contractBase58);
            if (tokenConfig == null || tokenConfig.getEnabled() != 1) {
                return null;
            }

            String data = value.getString("data");
            if (data == null || data.length() < 8) {
                return null;
            }

            // 检查方法签名 (transfer方法: a9059cbb)
            String methodSignature = data.substring(0, 8);
            if (!"a9059cbb".equals(methodSignature)) {
                return null;  // 不是transfer方法
            }

            // 解析参数
            // data格式: a9059cbb + 64位to地址 + 64位金额
            if (data.length() < 136) {
                log.warn("data长度不足: {}", data);
                return null;
            }

            // 提取from地址(owner_address)
            String fromAddressHex = value.getString("owner_address");
            String fromAddress = hexAddressToBase58(fromAddressHex);

            // 提取to地址 (从data中)
            //String toAddressHex = "41" + data.substring(32, 72).replaceFirst("^0+", "");
            String toAddressHex = "41" + data.substring(32, 72);
            String toAddress = hexAddressToBase58(toAddressHex);

            // 提取金额
            String amountHex = data.substring(72, 136);
            BigInteger amountWei = new BigInteger(amountHex, 16);
            BigDecimal amount = new BigDecimal(amountWei)
                    .divide(BigDecimal.TEN.pow(tokenConfig.getDecimals()), 18, RoundingMode.DOWN);

            TronTransaction transaction = new TronTransaction();
            transaction.setTxHash(txId);
            transaction.setBlockNumber(blockNumber);
            transaction.setFromAddress(fromAddress);
            transaction.setToAddress(toAddress);
            transaction.setAmount(amount);
            transaction.setTokenSymbol(tokenConfig.getTokenSymbol());
            transaction.setTokenContract(contractBase58);
            transaction.setTokenDecimals(tokenConfig.getDecimals());
            transaction.setTxType("TRC20");
            //交易状态 （1待完成，2已完成，3失败
            transaction.setStatus(isSuccess ? "2" : "3");
            transaction.setContractResult(isSuccess);
            transaction.setBlockTimestamp(timestamp);

            //log.debug("trc20解析: {} 转账: {} from {} to {}，txId {}",tokenConfig.getTokenSymbol(), amount, fromAddress, toAddress,txId);

            return transaction;

        } catch (Exception e) {
            log.error("从data解析TRC20转账失败: {}", txId, e);
        }

        return null;
    }

    /**
     * ============封装 接口  =======================================
     ****/

    /**
     * 获取大概的能量费用
     * @param address
     * @param contractAddress
     * @param method
     * @return
     */
    private BigDecimal getGasApi(String address, String contractAddress, String method) {
        if (StringUtils.isBlank(contractAddress)) {
            return BigDecimal.ZERO;
        }
        method=method.toLowerCase();
        Map<String, Object> map = new HashMap<>();
        map.put("owner_address", address);
        map.put("contract_address", contractAddress);
        if (method.equals("transferFrom")) {
            map.put("function_selector", "transferFrom(address,address,uint256)");
        }
        if (method.equals("approve")) {
            map.put("function_selector", "approve(address,uint256)");
        }
        if (method.equals("transfer")) {
            map.put("function_selector", "transfer(address,uint256)");
        } else {
            map.put("function_selector", method);
        }
        map.put("visible", "true");
        try {
            String body = executeRequest("/wallet/triggerconstantcontract", map, true);
            JSONObject node = JSON.parseObject(body);
            if (node != null && node.containsKey("energy_used")) {
                int energy_used = node.getIntValue("energy_used");
                return BigDecimal.valueOf(energy_used).multiply(BigDecimal.valueOf(1.2));
            }
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
        return BigDecimal.valueOf(4000L);
    }



    /**
     * 发送trx。
     * @param fromAddr
     * @param fromPrv
     * @param toAddr
     * @param amount
     * @return
     */

    private String sendTrx(String fromAddr,String fromPrv,String toAddr,BigDecimal amount){
        // 获取主币配置
        TokenPrices trxConfig = tokenPriceService.getTokenCacheByChain(CHAIN_TYPE, CHAIN_TYPE);
        if (trxConfig == null || trxConfig.getEnabled() != 1) {
            return null;
        }
        BigInteger amountWei = amount.multiply(BigDecimal.TEN.pow(trxConfig.getDecimals())).toBigInteger();
        try {
            ApiWrapper wrapper = getApiWrapper();
            KeyPair key=new KeyPair(fromPrv);
            Response.TransactionExtention transaction = wrapper.transfer(fromAddr, toAddr, amountWei.longValue());
            Chain.Transaction signTransaction = wrapper.signTransaction(transaction,key);
            setApiCount(wrapper);
            return ApiWrapper.toHex(signTransaction.toByteString());
        } catch (Exception ex) {
            ex.fillInStackTrace();
            return null;
        }

    }

    /**
     * 发送 erc20 包括 usdt usdc等
     * @param fromAddr
     * @param fromPrv
     * @param contractAddress
     * @param toAddr
     * @param amount
     * @return
     */
    private String sendErc20(String fromAddr,String fromPrv,String contractAddress,String toAddr,BigDecimal amount,BigDecimal gas){
        // 获取主币配置
        TokenPrices tokenConfig = tokenPriceService.getTokenCacheByContractAddress(contractAddress);
        if (tokenConfig == null || tokenConfig.getEnabled() != 1) {
            return null;
        }
        BigInteger amountWei = amount.multiply(BigDecimal.TEN.pow(tokenConfig.getDecimals())).toBigInteger();
        //ApiWrapper wrapper = getApiWrapper();
        try {
            BigDecimal gasLimit=  gas;
            Function function = new Function("transfer",
                    Arrays.asList(new Address(toAddr), new Uint256(amountWei)),
                    Arrays.asList(new TypeReference<Bool>() {
                    }));
            return signTransaction(fromAddr, fromPrv, gasLimit.toBigInteger(),tokenConfig.getTokenContract(), function);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }
// 写一个 由A付手续费，B地址归集到C ///////////////
    /**
     * 构造一个带 fee_payer 的 transfer 交易（A代付手续费）
     */
    private JSONObject buildTransferWithPayer(
            String ownerB,
            String payerA,
            String contract,
            String to,
            BigInteger amount,
            long feeLimit
    ) {

        // 构造参数
        Function function = new Function(
                "transfer",
                Arrays.asList(new Address(to), new Uint256(amount)),
                Collections.emptyList()
        );

        String encoded = Numeric.toHexString(
               FunctionEncoder.encode(function)
                        .getBytes()
        );

        JSONObject req = new JSONObject();
        req.put("owner_address", Base58ToHex(ownerB));
        req.put("contract_address", Base58ToHex(contract));
        req.put("function_selector", "transfer(address,uint256)");
        req.put("parameter", encodeParams(to, amount));
        req.put("fee_limit", feeLimit);
        req.put("call_value", 0);
        req.put("visible", true);

        // 调 /wallet/triggersmartcontract
        String body = executeRequest("/wallet/triggersmartcontract", req, true);
        JSONObject json = JSONObject.parseObject(body);

        if (!json.containsKey("transaction")) {
            throw new RuntimeException("构造失败：" + json);
        }

        JSONObject tx = json.getJSONObject("transaction");

        //  设置费付方（Trident 0.3.0 不支持，只能手动写 raw_data）
        tx.getJSONObject("raw_data").put("fee_payer", Base58ToHex(payerA));

        return tx;
    }

    /**
     * 使用 B 地址私钥签名交易
     */
    private JSONObject signRawTransaction(JSONObject tx, String privateKeyB) {
        JSONObject req = new JSONObject();
        req.put("transaction", tx);
        req.put("privateKey", privateKeyB);

        String body = executeRequest("/wallet/gettransactionsign", req, true);
        JSONObject json = JSONObject.parseObject(body);

        if (!json.getBoolean("result")) {
            throw new RuntimeException("签名失败：" + json);
        }

        return json.getJSONObject("transaction");
    }
    /**
     * 使用 A 地址代付手续费的 TRC20 转账（B→C）
     */
    public String transferTokenWithPayer(
            String ownerB,
            String prvB,
            String payerA,
            String contract,
            String toAddress,
            BigDecimal amount
    ) {
        // 代币精度
        TokenPrices cfg = tokenPriceService.getTokenCacheByContractAddress(contract);
        if (cfg == null || cfg.getEnabled() != 1) {
            return null;
        }

        BigInteger amountWei = amount.multiply(BigDecimal.TEN.pow(cfg.getDecimals())).toBigInteger();

        // 1) 构造原始交易（含 fee_payer=A）
        JSONObject tx = buildTransferWithPayer(
                ownerB,
                payerA,
                contract,
                toAddress,
                amountWei,
                100_000_000L
        );

        // 2) 使用 B 私钥签名
        JSONObject signed = signRawTransaction(tx, prvB);

        // 3) 广播
       // JSONObject req = new JSONObject();
        //req.put("transaction", signed);

        //String res = executeRequest("/wallet/broadcasttransaction", req, true);
        //JSONObject json = JSONObject.parseObject(res);
       // broadcastTransactionApi(signed.toJSONString());

        return signed.toJSONString();
    }
    ///// 结束///////////


/**
    public String createSignApprove(ApproveAccept accept) {
        BigInteger value;
        if (accept.getAmount() != null) {
            value = BlockUtils.toWei(accept.getAmount(), accept.getDecimals()).toBigInteger();
        } else {
            value = new BigInteger(accept.getAmountHex(), 16);
        }
        Function function = new Function("approve",
                Arrays.asList(new Address(accept.getAuthorAddress()), new Uint256(value)),
                Arrays.asList(new TypeReference<Bool>() {
                }));
        return signTransaction(accept.getFromAddress(), accept.getFromPrivateKey(), accept.getGasLimit(), accept.getContractAddress(), function);
    }**/

    /**
     * 创建签名
     * @param fromAddress
     * @param privateKey
     * @param gasLimit
     * @param contractAddress
     * @param function
     * @return
     */
    private String signTransaction(String fromAddress, String privateKey, BigInteger gasLimit, String contractAddress, Function function) {
        ApiWrapper wrapper = getApiWrapper();
        setApiCount(wrapper);
        Response.TransactionExtention transaction = wrapper.constantCall(fromAddress, contractAddress, function);
        TransactionBuilder builder = new TransactionBuilder(transaction.getTransaction());

        KeyPair key=new KeyPair(privateKey);
        builder.setFeeLimit(gasLimit.longValue());
        builder.setMemo("");
        Chain.Transaction signTransaction = wrapper.signTransaction(builder.build(),key);
        return ApiWrapper.toHex(signTransaction.toByteString());
    }



    /**
     * 广播交易
     */
    private String broadcastTransactionApi(String signedTxJson) {
        String url = "/wallet/broadcasttransaction";
        try {
            String body = executeRequest(url, signedTxJson, true);
            JSONObject data = JSON.parseObject(body);

            Boolean result = data.getBoolean("result");
            if (result != null && result) {
                String txid = data.getString("txid");
                log.info("交易广播成功: txid={}", txid);
                return txid;
            } else {
                String errorMsg = data.getString("message");
                throw new RuntimeException("广播失败: " + errorMsg);
            }

        } catch (Exception e) {
            log.error("广播交易失败", e);
            throw new RuntimeException("广播失败", e);
        }
    }

    /**
     * 这个数据少，速度块
     * 广播交易信息（Hex 格式）
     *
     * @param signatureData
     * @return 返回交易hash
     */
    private String broadcastHex(String signatureData) {
        Map<String, Object> data = new HashMap<>();
        data.put("transaction", signatureData);

        String results = executeRequest("/wallet/broadcasthex", data, true);
        log.info("trx广播：{}",results);
        JSONObject node = JSON.parseObject(results);
        if (node.getBoolean("result")) {
            return node.getString("txid");
        }else{
            return null;
        }
    }

    /**
     * 查询交易信息
     */
    private JSONObject getTransactionInfoApi(String txHash) {
        String url = "/wallet/gettransactioninfobyid";

        Map<String, Object> data = new HashMap<>();
        data.put("value", txHash);
        try {
            String body = executeRequest(url, data, true);
            return JSON.parseObject(body);

        } catch (Exception e) {
            log.error("查询交易信息失败: txHash={}", txHash, e);
            throw new RuntimeException("查询交易信息失败", e);
        }
    }

    /**
     * 查询交易详情
     */
    private JSONObject getTransactionByIdApi(String txHash) {
        String url = "/wallet/gettransactionbyid";

        Map<String, Object> data = new HashMap<>();
        data.put("value", txHash);
        try {
            String body = executeRequest(url, data, true);
            return JSON.parseObject(body);

        } catch (Exception e) {
            log.error("查询交易详情失败: txHash={}", txHash, e);
            throw new RuntimeException("查询交易详情失败", e);
        }
    }






    /**
     * 获取指定范围的区块
     */
    private JSONArray getBlockByLimitNext(Long startNum, Long endNum) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put("startNum", startNum);
            map.put("endNum", endNum);
            String body = executeRequest("/wallet/getblockbylimitnext", map, true);
            JSONObject json = JSON.parseObject(body);
            if (!json.isEmpty()) {
                return json.getJSONArray("block");
            }
            return null;
        } catch (Exception e) {
            log.error("获取范围区块 {} - {} 失败", startNum, endNum, e);
            return null;
        }
    }

    /**
     * 获取指定区块信息
     */
    private JSONObject getBlockByNumber(Long blockNumber) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("num", blockNumber);
            String body = executeRequest("/wallet/getblockbynum", params, true);
            return JSON.parseObject(body);

        } catch (Exception e) {
            log.error("获取区块 {} 失败", blockNumber, e);
            return null;
        }
    }
    // ==================== 工具方法 =======================================

    /**
     * 执行 HTTP 请求（支持 API Key 轮询）
     */
    private String executeRequest(String url, Object params, boolean isPost) {
        BcApiKeys apiKey = apiKeyService.getAvailableKey(CHAIN_TYPE);
        if (apiKey == null) {
            log.error(" 无可用的 TRON API Key");
            throw new RuntimeException("无可用的 TRON API Key");
        }
        try {
            String fullUrl = apiKey.getApiUrl() + url;
            Map<String, String> headers = new HashMap<>();
            String body = "";
            // 添加 API Key 到请求头
            if (StringUtils.isNotBlank(apiKey.getApiKey())) {
                headers.put("TRON-PRO-API-KEY", apiKey.getApiKey());
            }
            if (isPost) {
                // 设置请求体
                String jsonBody = "";
                if (params != null) {
                    if (params instanceof String) {
                        jsonBody = String.valueOf(params);
                    } else if (params instanceof Map) {
                        jsonBody = JSONObject.toJSONString(params);
                    } else {
                        jsonBody = JSONObject.toJSONString(params);
                    }
                }
                body = HttpUtils.sendPostJson(fullUrl, jsonBody, headers);
            } else {
                body = HttpUtils.sendGet(url, null, headers);
            }
            // 记录使用
            apiKeyService.recordUsage(apiKey.getId());

            return body;

        } catch (Exception e) {
            log.error("TRON API 请求失败: {}", url, e);
            throw new RuntimeException("TRON API 请求失败", e);
        }
    }



    private String hexAddressToBase58(String hexAddress) {
        try {
            if (hexAddress.startsWith("41")) {
                return org.tron.trident.utils.Base58Check.bytesToBase58(
                        Numeric.hexStringToByteArray(hexAddress)
                );
            } else if (hexAddress.startsWith("0x")) {
                String hex = "41" + hexAddress.substring(2);
                return org.tron.trident.utils.Base58Check.bytesToBase58(
                        Numeric.hexStringToByteArray(hex)
                );
            }
            return hexAddress;
        } catch (Exception e) {
            log.error("地址转换失败: {}", hexAddress, e);
            return hexAddress;
        }
    }
    // base58 => hex
    private String Base58ToHex(String base58) {
        return Numeric.toHexString(
                org.tron.trident.utils.Base58Check.base58ToBytes(base58)
        );
    }
    // 构造参数
    private String encodeParams(String to, BigInteger amount) {
        Function function = new Function(
                "transfer",
                Arrays.asList(new Address(to), new Uint256(amount)),
                Collections.emptyList()
        );
        return FunctionEncoder.encode(function);
    }
    private String topicToAddress(String topic) {
        try {
            String addressHex = topic.substring(24);
            String fullHex = "41" + addressHex;

            return org.tron.trident.utils.Base58Check.bytesToBase58(
                    Numeric.hexStringToByteArray(fullHex)
            );
        } catch (Exception e) {
            log.error("Topic 转地址失败: {}", topic, e);
            return "";
        }
    }


}
