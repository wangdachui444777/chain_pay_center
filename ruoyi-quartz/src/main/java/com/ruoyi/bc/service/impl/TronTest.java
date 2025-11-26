package com.ruoyi.bc.service.impl;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;

import com.ruoyi.bc.utils.TronTransactionSigner;
import com.ruoyi.blockchain.domain.TokenPrices;
import com.ruoyi.common.exception.UtilException;
import com.ruoyi.common.utils.StringUtils;
import org.tron.trident.abi.TypeReference;
import org.tron.trident.abi.datatypes.Address;
import org.tron.trident.abi.datatypes.Bool;
import org.tron.trident.abi.datatypes.Function;
import org.tron.trident.abi.datatypes.generated.Bytes32;
import org.tron.trident.abi.datatypes.generated.Uint256;
import org.tron.trident.core.ApiWrapper;
import org.tron.trident.core.key.KeyPair;
import org.tron.trident.core.transaction.TransactionBuilder;
import org.tron.trident.crypto.SECP256K1;
import org.tron.trident.proto.Chain;
import org.tron.trident.proto.Common;
import org.tron.trident.proto.Response;
import org.tron.trident.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TronTest {
    // 主网节点
    //private static final ApiWrapper client = ApiWrapper.ofNile("740f93bdedffed9a23de9361c2fc15d1cc86c7f5f50db9e7e77b43791a504714");

    // TRC20: USDT 主网合约地址（固定）
    private static final String USDT_CONTRACT = "TXYZopYRdj2D9XRtbG411XZZ3kM5VkAeBf";
    //Public Fullnode, maintained by official team
    public static final String FULLNODE_NILE = "grpc.nile.trongrid.io:50051";
    public static final String FULLNODE_NILE_SOLIDITY = "grpc.nile.trongrid.io:50061";

    /**
     * 委托资源给其他地址（Stake 2.0）
     * @param ownerAddress 资源提供方地址（主钱包）
     * @param receiverAddress 资源接收方地址（子钱包）
     * @param balance 委托的 TRX 数量（单位：SUN，1 TRX = 1,000,000 SUN）
     * @param resource 资源类型："ENERGY" 或 "BANDWIDTH"
     * @param lock 是否锁定（false=可随时取回，true=锁定3天）
     * @return 未签名的交易 JSON 字符串
     */
    private static String delegateResource(String ownerAddress, String receiverAddress, long balance,
                                           String resource,
                                           boolean lock
    ) {
        String url = "/wallet/delegateresource";
        Map<String, Object> params = new HashMap<>();
        params.put("owner_address", ownerAddress);
        params.put("receiver_address", receiverAddress);
        params.put("balance", balance);
        params.put("resource", resource);  // "ENERGY" 或 "BANDWIDTH"
        params.put("lock", lock);
        params.put("visible", true);

        try {
            String body = executeRequest(url, params);
            JSONObject data = JSON.parseObject(body);

            if (data.containsKey("Error")) {
                return null;
                //throw new RuntimeException("委托资源失败: " + data.getStr("Error"));
            }
            if (!data.containsKey("txID")) {
                throw new RuntimeException("委托资源失败: 响应中无 txID");
            }
            // 3. 解析交易数据
            String rawDataHex = data.getString("raw_data_hex");
            if (rawDataHex == null || rawDataHex.isEmpty()) {
                throw new RuntimeException("委托资源失败: raw_data_hex 为空");
            }
            //Chain.Transaction unsignedTxn =parseTransactionFromHex(rawDataHex);

            // 返回交易 JSON 字符串（需要签名后广播）
            return body;

        } catch (Exception e) {
            e.printStackTrace();
            // throw new RuntimeException("委托资源失败", e);
        }
        return null;
    }
    /**
     * 创建 TRC20 转账交易
     */
    private static String createTRC20TransactionApi(
            String fromAddress,
            String toAddress,
            String tokenContract,
            BigInteger amount
    ) {
        String url = "/wallet/triggersmartcontract";

        // 编码参数
        String parameter = encodeTransferParams(toAddress, amount);

        Map<String, Object> params = new HashMap<>();
        params.put("owner_address", fromAddress);
        params.put("contract_address", tokenContract);
        params.put("function_selector", "transfer(address,uint256)");
        params.put("parameter", parameter);
        params.put("fee_limit", 150_000_000);
        params.put("call_value", 0);
        params.put("visible", true);

        try {
            String body = executeRequest(url, params);
            JSONObject data = JSON.parseObject(body);

            if (data.containsKey("Error")) {
                throw new RuntimeException("创建交易失败: " + data.getString("Error"));
            }

            if (!data.containsKey("transaction")) {
                throw new RuntimeException("创建交易失败: 响应中无 transaction");
            }

            // 返回交易 JSON 字符串
            return data.getJSONObject("transaction").toString();

        } catch (Exception e) {
            throw new RuntimeException("创建交易失败", e);
        }
    }
    /**
     * 这个数据少，速度块
     * 广播交易信息（Hex 格式）
     * @param signatureData
     * // 2. 签名
     *  String signedTxJson = TronTransactionSigner.signTransaction(transactionJson, privateKey);
     *  String signatureData=jsonToHex(signedTxJson)
     * @return 返回交易hash
     */
    private static String broadcastHex(String signatureData){
        Map<String,Object> data=new HashMap<>();
        data.put("transaction", signatureData);

        String results= executeRequest("/wallet/broadcasthex",data);
        JSONObject node =JSON.parseObject(results);
        if(node.getBoolean("result")){
            return node.getString("txid");
        }
        throw new UtilException("广播交易失败:"+node.toString());
    }
    public static BigDecimal getGas(String address, String contractAddress, String method) {
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
            String body = executeRequest("/wallet/triggerconstantcontract", map);
            JSONObject node = JSON.parseObject(body);
            if (node != null && node.containsKey("energy_used")) {
                int energy_used = node.getIntValue("energy_used");
                return BigDecimal.valueOf(energy_used).multiply(BigDecimal.valueOf(1));
            }
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
        return BigDecimal.valueOf(4000L);
    }
    /**
     * 执行 HTTP 请求（支持 API Key 轮询）
     */
    private static String executeRequest(String url, Object params) {

        try {
            String fullUrl = "https://nile.trongrid.io" + url;
                String jsonBody="";
                // 设置请求体
                if (params != null) {

                    if (params instanceof String) {
                        jsonBody =String.valueOf(params);
                    } else if (params instanceof Map) {
                        jsonBody = JSON.toJSONString(params);
                    } else {
                        jsonBody = JSON.toJSONString(params);
                    }
                }
            String response =postJson(fullUrl,jsonBody);

            return response;

        } catch (Exception e) {
            throw new RuntimeException("TRON API 请求失败", e);
        }
    }
    public static String postJson(String url, String json) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .build();

        HttpRequest request = java.net.http.HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    /**
     * 编码 transfer 参数
     */
    private static String encodeTransferParams(String toAddress, BigInteger amount) {
        try {
            // 地址转 hex
            byte[] addressBytes = org.tron.trident.utils.Base58Check.base58ToBytes(toAddress);
            byte[] address32 = new byte[32];
            System.arraycopy(addressBytes, 1, address32, 12, 20);

            // 金额 padding
            String amountHex = String.format("%064x", amount);

            return org.tron.trident.utils.Numeric.toHexString(address32) + amountHex;

        } catch (Exception e) {
            throw new RuntimeException("编码 transfer 参数失败", e);
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
    private static String sendErc20(ApiWrapper wrapper,String fromAddr, String fromPrv, String contractAddress, String toAddr, BigDecimal amount){

        BigInteger amountWei = amount.multiply(BigDecimal.TEN.pow(6)).toBigInteger();
        //ApiWrapper wrapper = getApiWrapper();
        try {
            BigDecimal gasLimit=  getGas(fromAddr, contractAddress,"transfer");
            Function function = new Function("transfer",
                    Arrays.asList(new Address(toAddr), new Uint256(amountWei)),
                    Arrays.asList(new TypeReference<Bool>() {
                    }));
            return signTransaction(wrapper,fromAddr, fromPrv, gasLimit.toBigInteger(),contractAddress, function);
        } catch (Exception ex) {
            ex.fillInStackTrace();
            return null;
        }

    }


    private static String signTransaction(ApiWrapper wrapper,String fromAddress, String privateKey, BigInteger gasLimit, String contractAddress, Function function) {

        Response.TransactionExtention transaction = wrapper.constantCall(fromAddress, contractAddress, function);
        TransactionBuilder builder = new TransactionBuilder(transaction.getTransaction());

        KeyPair key=new KeyPair(privateKey);
        builder.setFeeLimit(gasLimit.longValue());
        builder.setMemo("");
        Chain.Transaction signTransaction = wrapper.signTransaction(builder.build(),key);
        return ApiWrapper.toHex(signTransaction.toByteString());
    }

    /**
     * 委托能量（简化方法）
     * @param ownerAddress 资源提供方
     * @param receiverAddress 资源接收方
     * @return 未签名的交易 JSON
     */
    public static String delegateEnergy(String ownerAddress, String receiverAddress) {
        // 动态基于当前资源页面显示：1 TRX = 76 ENERGY
        long energyPerTrx = 76;
        long energyNeeded = 30000; // 一次 TRC20 USDT 转账的能量需求
        long trxNeeded = (energyNeeded + (energyPerTrx - 1)) / energyPerTrx;
        long balance = trxNeeded * 1_000_000L; // TRX → SUN

        return delegateResource(ownerAddress, receiverAddress, balance, "ENERGY", false);
    }

    // 示例运行
    public static void main(String[] args) throws Exception {

        String fromPrv="b4e1ea2e77f3847ebb0c7953a6612207b23edbbafba19157cc4d779e5a0c50d8";
        String  fromAddr= "TYc9sdWrstzUfDvF3WUKd6BJ4ndY5G7rFZ"; // 有 USDT，无TRX

        String C_ADDR = "TQYTHCTbepdTiMzV5h41eJEYrS7orhm7EX"; // 收款地址
        long AMOUNT = 2_000_000L; // 1 USDT
        long trxAmount= 30 * 1_000_000L;
        String A_PRIV = "740f93bdedffed9a23de9361c2fc15d1cc86c7f5f50db9e7e77b43791a504714"; // 代付手续费地址
        String A_addr="TP6CqFPLbTdAuDxdhzoiFuBfM5WpMayFFM";
        try {
            ApiWrapper client =new  ApiWrapper(FULLNODE_NILE,FULLNODE_NILE_SOLIDITY,"");
            // 创建交易（不签名）

            BigDecimal gas= getGas(fromAddr,USDT_CONTRACT,"transfer");

            System.out.println("gas="+gas);
            BigDecimal gasPrice=new BigDecimal("420");
            BigDecimal gasLimit = gas.multiply(gasPrice).multiply(BigDecimal.TEN);
            System.out.println("gasLimit="+gasLimit);
            BigDecimal feeTrx= gasLimit.divide(BigDecimal.TEN.pow(6)).setScale(5, RoundingMode.DOWN);

            System.out.println("feeTrx="+feeTrx);
            long energyPerTrx = 76;
            long energyNeeded =65000; //30000; // 一次 TRC20 USDT 转账的能量需求
            long trxNeeded = (energyNeeded + (energyPerTrx - 1)) / energyPerTrx;
            System.out.println(trxNeeded);
            trxNeeded=energyNeeded/energyPerTrx;
            long balance = trxNeeded * 1_000_000L; // TRX → SUN
            System.out.println(balance);

            String sigendStr=sendErc20(client,fromAddr,fromPrv,USDT_CONTRACT,C_ADDR,new BigDecimal("2"));
         /**  Response.TransactionExtention txExt = client.delegateResource(
                    A_addr,
                   balance,
                    Common.ResourceCode.ENERGY.getNumber(),
                    fromAddr,
                    false // 不锁仓
            );**/
            String txid1 =broadcastHex(sigendStr);
            System.out.println("交易哈希: " + txid1);

            Response.TransactionExtention txExt=client.undelegateResource(A_addr,balance,Common.ResourceCode.ENERGY.getNumber(),fromAddr);

            //client.transfer()
            // 取出未签名交易
            Chain.Transaction unsignedTxn = txExt.getTransaction();
// 使用该 owner 地址的真实私钥签名
            KeyPair key1=new KeyPair(A_PRIV);
            // 3. B 地址签名（USDT 所有者签名）
            Chain.Transaction signedTx = client.signTransaction(unsignedTxn, key1);
             sigendStr=ApiWrapper.toHex(signedTx.toByteArray());
            // 广播到链上
            //String txid = client.broadcastTransaction(signedTx);
            String txid =broadcastHex(sigendStr);
            System.out.println("\n=== ✅ 交易广播成功 ===");
            System.out.println("交易哈希: " + txid);
        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
