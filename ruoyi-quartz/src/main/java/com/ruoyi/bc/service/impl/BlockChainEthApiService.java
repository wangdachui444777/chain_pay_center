package com.ruoyi.bc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.ruoyi.bc.component.ComponentBlockChainService;
import com.ruoyi.bc.config.ChainTypeConfirmations;
import com.ruoyi.bc.config.Web3jManager;
import com.ruoyi.bc.service.IBlockChainApiService;
import com.ruoyi.blockchain.domain.ApiBcTransaction;
import com.ruoyi.blockchain.domain.BcEnergyPaymentConfig;
import com.ruoyi.blockchain.domain.EthTransaction;
import com.ruoyi.blockchain.domain.TokenPrices;
import com.ruoyi.blockchain.service.ITokenPricesService;
import com.ruoyi.blockchain.service.impl.BlockchainRedisService;
import com.ruoyi.common.utils.EncryptUtils;
import com.ruoyi.common.utils.LogUtils;
import com.ruoyi.common.utils.security.Md5Utils;
import io.reactivex.functions.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.*;
public class BlockChainEthApiService implements IBlockChainApiService {

    private Web3jManager web3jManager;


    private final ITokenPricesService tokenPriceService;

    private final BlockchainRedisService redisService;

    private static final Logger log = LoggerFactory.getLogger(BlockChainEthApiService.class);

    // ERC20 标准方法
    public static final String TRANSFER_METHOD_ID = "a9059cbb";          // transfer(address,uint256)
    public static final String TRANSFER_FROM_METHOD_ID = "23b872dd";     // transferFrom(address,address,uint256)
    public static final String APPROVE_METHOD_ID = "095ea7b3";           // approve(address,uint256)

    // 期望的 input 长度
    public static final int TRANSFER_INPUT_LENGTH = 138;                 // 0x(2) + method(8) + addr(64) + value(64)
    public static final int TRANSFER_FROM_INPUT_LENGTH = 202;            // 0x(2) + method(8) + from(64) + to(64) + value(64)

    public BigDecimal _gasPrice=BigDecimal.ZERO;
    public BigDecimal _gasLimit=BigDecimal.ZERO;
    /** Transfer 事件签名 */
    private static final String TRANSFER_EVENT_SIGNATURE =
            "0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef";


    private static final String CHAIN_TYPE="ETH";

    // 常见 DeFi 合约黑名单
    private static final Set<String> CONTRACT_BLACKLIST = new HashSet<>(Arrays.asList(
            // Wrapped Ether
            "0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2",

            // Uniswap
            "0x68b3465833fb72a70ecdf485e0e4c7bd8665fc45", // Universal Router
            "0xe592427a0aece92de3edee1f18e0157c05861564", // SwapRouter
            "0x7a250d5630b4cf539739df2c5dacb4c659f2488d", // UniswapV2Router02

            // Curve
            "0xbebc44782c7db0a1a60cb6fe97d0b483032ff1c7", // 3pool

            // 1inch
            "0x1111111254eeb25477b68fb85ed929f73a960582"

            // 可以继续添加更多...
    ));

    // 单例实例（volatile 保证线程安全）
    private static volatile BlockChainEthApiService instance;
    // 构造方法私有化
    private BlockChainEthApiService(ITokenPricesService tokenPriceService, BlockchainRedisService redisService,Web3jManager web3jManager) {
        this.tokenPriceService = tokenPriceService;
        this.redisService = redisService;
        this.web3jManager = web3jManager;
        //初始化币种配置
        tokenPriceService.initTokenConfigCache(CHAIN_TYPE);
    }
    /**
     * 获取单例实例（线程安全 + 依赖注入）
     */
    public static BlockChainEthApiService getInstance(ITokenPricesService tokenPriceService, BlockchainRedisService redisService ,Web3jManager web3jManager) {
        if (instance == null) {
            synchronized (BlockChainTronApiService.class) {
                if (instance == null) {
                    instance = new BlockChainEthApiService(tokenPriceService, redisService,web3jManager);
                }
            }
        }
        return instance;
    }



    @Override
    public Long getLatestBlockNumber() {

        Web3j web3j = web3jManager.getWeb3j(CHAIN_TYPE); // 或 "TRX", "BSC", "POLYGON"
        try {
            web3jManager.setApiCount(web3j);
            return web3j.ethBlockNumber().send().getBlockNumber().longValue();
        } catch (Exception e) {
            throw new RuntimeException("获取区块高度失败", e);
        }
    }

    @Override
    public List<ApiBcTransaction> getBlockTransactions(Long startBlockNum, Long endBlockNum, ComponentBlockChainService handlerChainService) {
        // 获取 API Key
        Web3j web3j = web3jManager.getWeb3j(CHAIN_TYPE); // 或 "TRX", "BSC", "POLYGON"
        List<ApiBcTransaction> rs = new ArrayList<>();
        try {
             //单块查询模式
            startBlockNum=startBlockNum+1; //单块模式需要往后推一个，因为缓存的记录
            for (long blockNum = startBlockNum; blockNum <= endBlockNum; blockNum++) {
                EthBlock.Block block = web3j.ethGetBlockByNumber(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(blockNum)),
                        true
                ).send().getBlock();
                web3jManager.setApiCount(web3j);
                // 解析内部交易和ERC20日志
                //System.out.println(BigInteger.valueOf(blockNum));
               // System.out.println(web3j);
                List<ApiBcTransaction> bcTransactionList= doHandlerBlock(block);
                //log.debug("有效交易笔数{}", bcTransactionList.size());
              //处理数据库结果
                handlerChainService.HandlerSaveTransaction(bcTransactionList,CHAIN_TYPE);
                Thread.sleep(200);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return rs;
        }
        return rs;
    }

    /**
     * 计算所需要的转账主币数量
     * @param gasPrice
     * @param gas
     * @return
     */
    public BigDecimal getFee(BigDecimal gasPrice,BigDecimal gas,boolean hasEnergy){
        try {
            BigDecimal eth = gas.multiply(gasPrice);
            BigDecimal feeEth= eth.divide(BigDecimal.TEN.pow(18)).setScale(6, RoundingMode.UP);
            return feeEth;
        }catch (Exception e){
            // 异常兜底，返回 0.002 ETH（基本 ERC20 足够）
            return new BigDecimal("0.002");
        }
    }

    /**
     * 发送手续费
     * @param fromAddr
     * @param fromPrv
     * @param toAddr
     * @param amount
     * @param gasPrice
     * @param gas
     * @return
     */
    public String sendTxFee(String fromAddr,String fromPrv,String toAddr,BigDecimal amount,BigDecimal gasPrice,BigDecimal gas,boolean hasEnergy,BcEnergyPaymentConfig energyConfig){
        String signedData= sendMain(fromAddr,fromPrv,toAddr,amount,gasPrice,gas);
        if(signedData!=null){
            return broadcastTransaction(signedData);
        }
        return null;
    }

    /**
     * 获取 gas费用
     * @param fromAddr
     * @param contractAddr
     * @param method
     * @return
     */
    public BigDecimal getGas(String fromAddr ,String contractAddr,String method,String toAddr, BigDecimal amount){
        return getGasLimit(contractAddr,method);
    }

    /**
     * 获取gas价格
     * @return
     */
    public BigDecimal getGasPrice(){
        Web3j web3j=web3jManager.getWeb3j(CHAIN_TYPE);
        return getGasPriceApi(web3j);

    }

    /**
     * 主币转账
     * @param fromAddr
     * @param fromPrv
     * @param toAddr
     * @param amount
     * @param gasPrice
     * @param gas
     * @return
     */
    public String transfer(String fromAddr,String fromPrv,String toAddr,BigDecimal amount,BigDecimal gasPrice,BigDecimal gas){
        String signedData= sendMain(fromAddr,fromPrv,toAddr,amount,gasPrice,gas);
        if(signedData!=null){
            return broadcastTransaction(signedData);
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
     * @param gasPrice
     * @param gas
     * @return
     */
    public String transferToken(String fromAddr,String fromPrv,String contractAddr,String toAddr,BigDecimal amount,BigDecimal gasPrice,BigDecimal gas){
        String signedData=sendErc20(fromAddr,fromPrv,contractAddr,toAddr,amount,gasPrice,gas);
        if(signedData!=null){
            return broadcastTransaction(signedData);
        }
        return null;
    }


    /**
     * 检测交易后的状态
     * @param txHash
     * @return
     */
    public Map<String, Object> checkTxStatus(String txHash){
        Web3j web3j= web3jManager.getWeb3j(CHAIN_TYPE);
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("blockNumber", null);
        result.put("gasUsed", 0);
        result.put("mainCoin", 0);
        // 查询 receipt
        EthGetTransactionReceipt receiptResponse = null;
        try {
            receiptResponse = web3j.ethGetTransactionReceipt(txHash).send();
            Optional<TransactionReceipt> receiptOpt = receiptResponse.getTransactionReceipt();
            if (!receiptOpt.isPresent()) {
                return result;
            }
            TransactionReceipt receipt = receiptOpt.get();
            BigInteger blockNumber = receipt.getBlockNumber();
            boolean success = "0x1".equalsIgnoreCase(receipt.getStatus());

           String effectiveGasPrice= receipt.getEffectiveGasPrice();
            BigInteger gasPrice =BigInteger.ZERO;
            // 如果为 null，则降级为原始 gasPrice
            if (effectiveGasPrice == null) {
                gasPrice = web3j.ethGetTransactionByHash(txHash).send()
                        .getTransaction().get().getGasPrice();
            }else{
                gasPrice =Numeric.toBigInt(effectiveGasPrice);
            }
            BigInteger gasUsed =receipt.getGasUsed();
            // ③ 实际手续费 = gasUsed * gasPrice（单位 wei）
            BigInteger totalWei =gasUsed.multiply(gasPrice);
            // 转换为 ETH（或链主币，比如 BNB/MATIC）
            BigDecimal costMainCoin = new BigDecimal(totalWei).divide(BigDecimal.TEN.pow(18), 8, RoundingMode.HALF_UP);

            result.put("success", success);
            result.put("blockNumber", blockNumber);
            result.put("gasUsed", gasUsed);
            result.put("mainCoin", costMainCoin);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;

    }

    public Map<String, Object> checkTxStatus(String txHash, BcEnergyPaymentConfig energyConfig){
        return checkTxStatus(txHash);
    }

    //===============私有方法=========
    private List<ApiBcTransaction> doHandlerBlock(EthBlock.Block block){
        List<ApiBcTransaction> rs = new ArrayList<>();
        // 处理区块...
        if (block == null) {
            log.warn("区块 {} 不存在");
            return rs;
        }

        List<EthBlock.TransactionResult> transactions = block.getTransactions();
        if (transactions == null || transactions.isEmpty()) {
            log.warn("transactions 为空");
            return rs;
        }
         log.debug("区块 {} 包含 {} 笔交易", block.getNumber(), transactions.size());
        Long timesTamp=block.getTimestamp().longValue();
        // 解析每笔交易
        for (EthBlock.TransactionResult txResult : transactions) {
            EthBlock.TransactionObject tx = (EthBlock.TransactionObject) txResult.get();

            ApiBcTransaction bcTx=doHandlerTransfer(tx,timesTamp*1000);
            if(bcTx!=null){
                rs.add(bcTx);
            }

        }
        return rs;

    }
    protected ApiBcTransaction doHandlerTransfer(Transaction tx,Long blockTimesTamp) {
        try {

            String input = tx.getInput();
            int length = input.length();
            if (length != 2 && length < 10) {
                return null;
            }
            if (length > 10000) {
                return null;
            }
            String from = tx.getFrom();
            String to = tx.getTo();

            if (from==null || to==null){
                return null;
            }
            String hash = tx.getHash();
            BigInteger blockNumber = tx.getBlockNumber();
            String method = length == 2 ? input : input.substring(2, 10);//合约方法
            BigInteger ethValue = tx.getValue();
            //如果是eth转账
            boolean hasEthTransfer = ethValue.compareTo(BigInteger.ZERO) > 0;
            String contractAddress = null;//合约地址
            BigDecimal amount = BigDecimal.ZERO;
            TokenPrices tokenConfig = null;
            String txType = CHAIN_TYPE;

            // 1. 检查是否在黑名单中
            if (CONTRACT_BLACKLIST.contains(to.toLowerCase())) {
                // log.debug("屏蔽黑名单合约: {} -> {}", tx.getHash(), to);
                return null;
            }

            //仅ETH 简单转账
            if (hasEthTransfer && "0x".equals(method)) {
                // 获取主币配置
                tokenConfig = tokenPriceService.getTokenCacheByChain(CHAIN_TYPE, CHAIN_TYPE);
                if (tokenConfig == null || tokenConfig.getEnabled() != 1) {
                    return null;
                }
                amount = Convert.fromWei(
                        new BigDecimal(ethValue),
                        Convert.Unit.ETHER
                );
            } else {

                if (length != TRANSFER_INPUT_LENGTH && length != TRANSFER_FROM_INPUT_LENGTH) {
                    return null;
                }
                txType = "ERC20";
                contractAddress = to;
                String v = input.substring(input.length() - 64);
                ethValue = new BigInteger(v, 16);
                //合约交易
                if (TRANSFER_METHOD_ID.equals(method)) {
                    to = "0x".concat(input.substring(34, 74));
                } else if (TRANSFER_FROM_METHOD_ID.equals(method)) {
                    from = "0x".concat(input.substring(34, 74));
                    to = "0x".concat(input.substring(98, 138));
                } else {
                    return null;
                }
                tokenConfig = tokenPriceService.getTokenCacheByContractAddress(contractAddress);
                if (tokenConfig == null || tokenConfig.getEnabled() != 1) {
                    return null;
                }
                amount = new BigDecimal(ethValue).divide(BigDecimal.TEN.pow(tokenConfig.getDecimals()), 18, RoundingMode.DOWN);

            }
            // 定义最小有效值： 0.000001 ETH
            BigDecimal MIN_VALUE = new BigDecimal("0.000001");
            // 判断是否有意义
            if (amount.compareTo(MIN_VALUE) < 0) {
                // 太小，无意义，直接忽略
                return null;
            }
            // 1. 检查是否是监听地址
            Long addressId = redisService.getAddressId(CHAIN_TYPE, to);
            if (addressId == null) {
                //log.debug("地址 {} 不在监听列表", toAddress);
                return null;
            }

            //log.debug("解析转账: {}  币种：{} from {} to {}，txHash {}", amount,txType, tx.getFrom(), tx.getTo(),hash);
            // 构建交易对象
            boolean success = true;//getTxStatus(hash);
            ApiBcTransaction bcTransaction = new ApiBcTransaction();
            bcTransaction.setTxHash(hash);
            bcTransaction.setBlockNumber(blockNumber.longValue());
            if (blockTimesTamp != null) {
                bcTransaction.setBlockTimestamp(blockTimesTamp);
            }
            bcTransaction.setFromAddress(from);
            bcTransaction.setToAddress(to);
            bcTransaction.setAmount(amount);
            bcTransaction.setTokenSymbol(tokenConfig.getTokenSymbol());
            bcTransaction.setTokenContract(contractAddress);
            bcTransaction.setTokenDecimals(tokenConfig.getDecimals());
            bcTransaction.setTxType(txType);
            bcTransaction.setStatus(success ? "2" : "3");
            bcTransaction.setContractResult(success);
            return bcTransaction;
        }catch (Exception e){
            return null;
        }

    }
    /**
     * 获取区块时间
     * @param blockNumber
     * @return
     */
    protected Long getBlockTime(BigInteger blockNumber,Web3j web3j){
        try {
            //Web3j web3j = web3jManager.getWeb3j(CHAIN_TYPE);
            web3jManager.setApiCount(web3j);
            EthBlock.Block block = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(blockNumber), false).send().getBlock();
            return block.getTimestamp().longValue()*1000;
        }
        catch (Exception ex){
            ex.fillInStackTrace();
            return System.currentTimeMillis();
        }
    }

    /**
     * 获取交易状态
     * @param txHash
     * @return
     */
    protected boolean getTxStatus(String txHash) {
        Web3j web3j = web3jManager.getWeb3j(CHAIN_TYPE);
        // 1. 获取交易收据
        TransactionReceipt receipt = null;
        try {
            receipt = web3j.ethGetTransactionReceipt(txHash)
                    .send()
                    .getTransactionReceipt()
                    .orElse(null);
            web3jManager.setApiCount(web3j);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (receipt != null) {
            String status = receipt.getStatus();
           return "0x1".equals(status);

        }
        return false;
    }
    private String sendErc20(String fromAddr,String fromPrv,String ContractAddr,String toAddr,BigDecimal amount,BigDecimal gasPrice,BigDecimal gasLimit){
        // 获取主币配置
        TokenPrices tokenConfig = tokenPriceService.getTokenCacheByContractAddress(ContractAddr);
        if (tokenConfig == null || tokenConfig.getEnabled() != 1) {
            return null;
        }
        amount = amount.multiply(BigDecimal.TEN.pow(tokenConfig.getDecimals()));

        //BigInteger value= Convert.toWei(amount,BigDecimal.TEN.pow(tokenConfig.getDecimals())).toBigInteger();
        BigInteger value=amount.toBigInteger();
        //合约交易
        Function function = new Function(
                "transfer",
                Arrays.<Type>asList(new Address(toAddr),new Uint256(value)),
                Collections.<TypeReference<?>>emptyList());
        String data = FunctionEncoder.encode(function);
        return signTransaction(fromAddr,fromPrv,gasPrice.toBigInteger(),gasLimit.toBigInteger(),ContractAddr,data);
    }

    /**
     * 发送主币
     * @param fromAddr
     * @param fromPrv
     * @param toAddr
     * @param amount
     * @param gasPrice
     * @param gasLimit
     * @return
     */
    private String sendMain(String fromAddr,String fromPrv,String toAddr,BigDecimal amount,BigDecimal gasPrice,BigDecimal gasLimit){
        // 获取主币配置
        TokenPrices tokenConfig = tokenPriceService.getTokenCacheByChain(CHAIN_TYPE,CHAIN_TYPE);
        if (tokenConfig == null || tokenConfig.getEnabled() != 1) {
            return null;
        }
        amount = amount.multiply(BigDecimal.TEN.pow(tokenConfig.getDecimals()));
        BigInteger value=amount.toBigInteger();
        Credentials credentials = getCredentials(fromPrv);
        BigInteger nonce= getNonce(fromAddr);

        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice.toBigInteger(),gasLimit.toBigInteger(), toAddr, value);
        return signTransaction(rawTransaction,credentials);
    }


    /**
     *  用来erc20 签名
     * @param fromAddress
     * @param privateKey
     * @param gasPrice
     * @param gasLimit
     * @param to
     * @param data
     * @return
     */
    private String signTransaction(String fromAddress,String privateKey,BigInteger gasPrice,BigInteger gasLimit,String to,String data){
        Credentials credentials = getCredentials(privateKey);
        BigInteger nonce=getNonce(fromAddress);

        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice,gasLimit, to, data);
        return signTransaction(rawTransaction, credentials);
    }

    private String signTransaction( RawTransaction rawTransaction, Credentials credentials) {
        switch (CHAIN_TYPE){
            case ChainTypeConfirmations.CHAIN_MATIC:
                return Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, 137, credentials));
            case ChainTypeConfirmations.CHAIN_AVAX:
                return Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, 43114, credentials));
            case ChainTypeConfirmations.CHAIN_BNB:
                return Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, 56, credentials));
            case ChainTypeConfirmations.CHAIN_FTM:
                return Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, 250, credentials));
            case ChainTypeConfirmations.CHAIN_OETH:
                return Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, 10, credentials));
            default:
                return Numeric.toHexString(TransactionEncoder.signMessage(rawTransaction, credentials));
        }
    }
    /**
     * 广播交易
     * @param hexString 已完成签名的数据
     * @return
     */
    private String broadcastTransaction(String hexString){
        try {
            Web3j web3j=web3jManager.getWeb3j(CHAIN_TYPE);
            EthSendTransaction transaction = web3j.ethSendRawTransaction(hexString).send();
            // 检查错误
            if (transaction.hasError()) {
                log.error("ETH 广播失败: code={}, message={}",
                        transaction.getError().getCode(),
                        transaction.getError().getMessage());
                return null;
            }
            String hash = transaction.getTransactionHash();
            web3jManager.setApiCount(web3j);
            if(StringUtils.isNotBlank(hash)){
                return hash;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
        return null;
    }
    /**
     * 获取gas-price
     * @return
     */
    private  BigDecimal getGasPriceApi(Web3j client) {
        try {
            EthGasPrice ethGasPrice = client.ethGasPrice().sendAsync().get();
            web3jManager.setApiCount(client);
            if (ethGasPrice == null) {
                LogUtils.getErrorLog().error("GetGasPriceError");
                return null;
            }
            BigDecimal gasPrice=new BigDecimal(ethGasPrice.getGasPrice());
           return  gasPrice.multiply(BigDecimal.valueOf(1.2));
        } catch (Throwable t) {
            LogUtils.getErrorLog().error(t.getMessage(), t);
        }
        return new BigDecimal("4100000000");
    }

    private BigDecimal getGasLimit(String contractAddress,String method) {
        BigDecimal gasLimit = BigDecimal.ZERO;
        if (StringUtils.isNotBlank(contractAddress)) {
            if ("transferFrom".equalsIgnoreCase(method)) {
                gasLimit = new BigDecimal("100000");
            } else {
                gasLimit = new BigDecimal("64000");
            }
        } else {
            gasLimit = new BigDecimal("21000");
        }
        return gasLimit;
    }

    /**
     *
     * @param address
     * @return
     */
    public BigInteger getNonce(String address) {
        try {
            Web3j web3j=web3jManager.getWeb3j(CHAIN_TYPE);
            EthGetTransactionCount getNonce = web3j.ethGetTransactionCount(address, DefaultBlockParameterName.PENDING).send();
            BigInteger count=getNonce.getTransactionCount();
            web3jManager.setApiCount(web3j);
            return count;
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return null;

    }

    private final Map<String,Credentials> _mapCredentials=new HashMap<>();
    private final Object lock_obj=new Object();
    private Credentials getCredentials(String privateKey){
        String key= Md5Utils.hash(privateKey);
        if(_mapCredentials.containsKey(key)){
            return _mapCredentials.get(key);
        }
        synchronized (lock_obj){
            if(_mapCredentials.containsKey(key)){
                return _mapCredentials.get(key);
            }
            Credentials credentials = Credentials.create(privateKey);
            _mapCredentials.put(key,credentials);
            return credentials;
        }
    }



}
