package com.ruoyi.bc.service.impl;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.protobuf.ByteString;

import com.ruoyi.bc.utils.TronTransactionSigner;
import com.ruoyi.blockchain.domain.TokenPrices;
import com.ruoyi.common.exception.UtilException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import org.tron.trident.abi.FunctionEncoder;
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
    //private static final String USDT_CONTRACT = "TXYZopYRdj2D9XRtbG411XZZ3kM5VkAeBf";
    private static final String USDT_CONTRACT = "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t";
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
    public static BigDecimal getGas(String address, String contractAddress, String method, String toAddress, BigInteger amount) {
        if (StringUtils.isBlank(contractAddress)) {
            return BigDecimal.ZERO;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("owner_address", address);
        map.put("contract_address", contractAddress);
        String functionSelector;
        if (method.equalsIgnoreCase("transferFrom")) {
            functionSelector = "transferFrom(address,address,uint256)";
        } else if (method.equalsIgnoreCase("approve")) {
            functionSelector = "approve(address,uint256)";
        } else if (method.equalsIgnoreCase("transfer")) {
            functionSelector = "transfer(address,uint256)";
        } else {
            functionSelector = method;
        }
        map.put("function_selector", functionSelector);
        if (StringUtils.isNotBlank(toAddress) && amount != null) {
            String parameter = encodeFunctionParams(toAddress, amount);
            if (StringUtils.isNotBlank(parameter)) {
                map.put("parameter", parameter);
            }
        }
        map.put("visible", "true");
        try {
            String body = executeRequest("/wallet/triggerconstantcontract", map);
            JSONObject node = JSON.parseObject(body);
            if (node != null && node.containsKey("energy_used")) {
                int energy_used = node.getIntValue("energy_used");
                System.out.println("原数据："+energy_used);
                return BigDecimal.valueOf(energy_used); //BigDecimal.valueOf(energy_used).multiply(BigDecimal.valueOf(1.1));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("TRON 能量估算失败，");
        }
        // 兜底能量（估算失败时使用，避免手续费不足）
        return BigDecimal.valueOf(33000L);
    }

    private static String encodeFunctionParams(String toAddress, BigInteger amount) {
        try {
            Function function = new Function("transfer",
                    Arrays.asList(new Address(toAddress), new Uint256(amount)),
                    Arrays.asList(new TypeReference<Bool>() {
                    }));
            String encoded = FunctionEncoder.encode(function);
            if (StringUtils.isBlank(encoded)) {
                return null;
            }
            if (encoded.startsWith("0x")) {
                encoded = encoded.substring(2);
            }
            if (encoded.length() <= 8) {
                return null;
            }
            return encoded.substring(8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * 执行 HTTP 请求（支持 API Key 轮询）
     */
    private static String executeRequest(String url, Object params) {

        try {
           // String fullUrl = "https://nile.trongrid.io" + url;
            String fullUrl = "https://api.trongrid.io" + url;
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
    public static BigDecimal getFee(BigDecimal gasPrice,BigDecimal gas,boolean hasEnergy){
        try {
            if (hasEnergy){
                return new BigDecimal("65000");
            }
            BigDecimal minFee = gas.compareTo(new BigDecimal("100000")) >= 0
                    ? new BigDecimal("14")
                    : new BigDecimal("7");
            BigDecimal maxFee = new BigDecimal("14");
            if (gasPrice != null && gas != null) {
                BigDecimal feeSun = gas.multiply(gasPrice);
                BigDecimal feeTrx = feeSun.divide(BigDecimal.TEN.pow(6), 6, RoundingMode.UP);
                // 适度放大，避免手续费不足
                feeTrx = feeTrx.multiply(new BigDecimal("1.05")).setScale(5, RoundingMode.UP);
                if (feeTrx.compareTo(minFee) < 0) {
                    feeTrx = minFee;
                }
                if (feeTrx.compareTo(maxFee) > 0) {
                    feeTrx = maxFee;
                }
                return feeTrx;
            }
            return minFee;
        }catch (Exception e){
            return new BigDecimal("14");
        }
    }
    private static BigDecimal getEnergyPrice() {
        try {
            String body = executeRequest("/wallet/getchainparameters", null);
            JSONObject json = JSON.parseObject(body);
            if (json == null || !json.containsKey("chainParameter")) {
                return new BigDecimal("420");
            }
            for (Object itemObj : json.getJSONArray("chainParameter")) {
                JSONObject item = (JSONObject) itemObj;
                if ("getEnergyFee".equalsIgnoreCase(item.getString("key"))) {
                    return new BigDecimal(item.getString("value"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new BigDecimal("420");
    }
    public static String postJson(String url, String json) throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put("TRON-PRO-API-KEY","c5c4b967-e506-4178-92e0-d412f0e4e3b0");
        String body = HttpUtils.sendPostJson(url, json, headers);
        return body;
    }





    // 示例运行
    public static void main(String[] args) throws Exception {

        String fromPrv="b4e1ea2e77f3847ebb0c7953a6612207b23edbbafba19157cc4d779e5a0c50d8";
        String  fromAddr= "TUyxYgMW2bAsLTs439pijExd2sUcQDNQTK"; // 有 USDT，无TRX

        String C_ADDR = "TMoY5pCvtZFUQo14HbcKjK2yYTh7bbRAuo"; // 收款地址
        long AMOUNT = 1l; // 1 USDT
        long trxAmount= 30 * 1_000_000L;
        String A_PRIV = "740f93bdedffed9a23de9361c2fc15d1cc86c7f5f50db9e7e77b43791a504714"; // 代付手续费地址
        String A_addr="TP6CqFPLbTdAuDxdhzoiFuBfM5WpMayFFM";
        try {
           // ApiWrapper client =new  ApiWrapper(FULLNODE_NILE,FULLNODE_NILE_SOLIDITY,"");

            // 创建交易（不签名）

            BigDecimal gas= getGas(fromAddr,USDT_CONTRACT,"transfer", C_ADDR, BigInteger.valueOf(AMOUNT));

            System.out.println("gas="+gas);
            BigDecimal gasPrice=getEnergyPrice();
            BigDecimal feeTrx=getFee(gasPrice,gas,false);

            System.out.println("feeTrx="+feeTrx);

        }catch (Exception e){
            e.printStackTrace();
        }


    }
}
