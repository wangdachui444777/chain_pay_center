package com.ruoyi.bc.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.bitcoinj.core.Base58;
import org.tron.trident.abi.FunctionEncoder;
import org.tron.trident.abi.datatypes.Address;
import org.tron.trident.abi.datatypes.Function;
import org.tron.trident.abi.datatypes.generated.Uint256;
import org.tron.trident.core.ApiWrapper;
import org.tron.trident.core.transaction.TransactionBuilder;
import org.tron.trident.crypto.Hash;
import org.tron.trident.crypto.SECP256K1;
import org.tron.trident.proto.Chain;
import org.tron.trident.proto.Response;
import org.tron.trident.utils.Base58Check;
import org.tron.trident.utils.Numeric;

import java.math.BigInteger;
import java.util.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class TestTronPayer {

    static final String NODE = "https://nile.trongrid.io";

    // ======= 修改为你的地址 =======
    static final String B_ADDRESS = "TKzMkkC3AAMXAbmbBSacj2xWGE3yGRZxxn";
    static final String B_PRIVATE_KEY = "04de9db2c400523906fb7298ce57c6cefc593da14fe0e4b264e3af6aed1568c4";
    static final String A_ADDRESS = "TP6CqFPLbTdAuDxdhzoiFuBfM5WpMayFFM";
    static final String USDT_CONTRACT = "TXYZopYRdj2D9XRtbG411XZZ3kM5VkAeBf";

    public static void main(String[] args) {

        String hex = Numeric.toHexString(Base58.decodeChecked("TXYZopYRdj2D9XRtbG411XZZ3kM5VkAeBf"));
        System.out.println(hex);
        String txid = sendByPayer(
                B_ADDRESS,
                B_PRIVATE_KEY,
                A_ADDRESS,
                USDT_CONTRACT,
                "TQYTHCTbepdTiMzV5h41eJEYrS7orhm7EX",
                new BigInteger("4000000") // 1 USDT (6 decimals)
        );

        System.out.println("TXID = " + txid);
    }



    static String sendByPayer(
            String ownerB,
            String prvB,
            String payerA,
            String contract,
            String to,
            BigInteger amount
    ) {

        JSONObject tx = buildTransfer(ownerB, payerA, contract, to, amount);
        JSONObject signed = sign(tx, prvB);
        return broadcast(signed);
    }


    /** 1) 构造带 fee_payer 的交易 */
    static JSONObject buildTransfer(String ownerB, String payerA, String contract, String to, BigInteger amount) {

        System.out.println("===传入的原始参数===");
        System.out.println("ownerB="+ownerB);
        System.out.println("payerA="+payerA);
        System.out.println("contract="+contract);
        System.out.println("to="+to);
        System.out.println("amount="+amount);

        JSONObject req = new JSONObject();
        req.put("owner_address", toHex(ownerB));
        req.put("contract_address", toHex(contract));
        req.put("function_selector", "transfer(address,uint256)");
        System.out.println("ENCODE INPUT to = " + to);
        req.put("parameter", encodeParams(to, amount));
        req.put("fee_limit", 100_000_000L);
        req.put("call_value", 0);
        req.put("visible", false);
        req.put("permission_id", 0);
        System.out.println("经过处理后，传入参数req："+req.toJSONString());
        JSONObject res = post("/wallet/triggersmartcontract", req);
        System.out.println("请求triggersmartcontract返回res："+res.toJSONString());
        JSONObject tx = res.getJSONObject("transaction");

        // ⭐⭐ 关键：设置代付方
        tx.getJSONObject("raw_data").put("fee_payer", toHex(payerA));

        return tx;
    }


    /** 2) 签名 */
    static JSONObject sign(JSONObject tx, String privateKey) {
        JSONObject req = new JSONObject();
        req.put("transaction", tx);
        req.put("privateKey", privateKey);
        System.out.println("sign 传入参数===");
        System.out.println("req="+req);
        JSONObject res = post("/wallet/gettransactionsign", req);
        System.out.println("返回="+res);
        return res.getJSONObject("transaction");
    }


    /** 3) 广播 */
    static String broadcast(JSONObject signed) {
        JSONObject req = new JSONObject();
        req.put("transaction", signed);

        JSONObject res = post("/wallet/broadcasttransaction", req);

        System.out.println("broadcast res = " + res);

        return res.getString("txid");
    }


    /** address param must remove 41 prefix */
    static String encodeParams(String toBase58, BigInteger amount) {

        // Base58 -> 41xxxx hex
        String hex = toHex(toBase58);

        System.out.println("hex to="+hex);
        // remove leading 41
       // String raw = hex.substring(2);
       // System.out.println("remove leading 41="+raw);
        Function f = new Function(
                "transfer",
                Arrays.asList(new Address(hex), new Uint256(amount)),
                Collections.emptyList()
        );

        String encoded = FunctionEncoder.encodeConstructor(f.getInputParameters());
        System.out.println("encoded 之后"+encoded);
        return  encoded;//encoded.startsWith("0x") ? encoded.substring(2) : encoded;
    }

    /** base58 to hex */
    static String toHex(String base58) {

        // convert Base58 → 41xxxx hex no 0x prefix
        return Numeric.toHexString(
                Base58Check.base58ToBytes(base58)
        ).substring(2);
    }


    /** 简单 POST */
    static JSONObject post(String path, JSONObject obj) {
        try {
            URL url = new URL(NODE + path);
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("POST");
            c.setRequestProperty("Content-Type", "application/json");
            c.setRequestProperty("TRON-PRO-API-KEY", "5f87c17c-5829-40f7-a6bf-560f49975a95");

            c.setDoOutput(true);

            c.getOutputStream().write(obj.toJSONString().getBytes());

            BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);

            return JSONObject.parseObject(sb.toString());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
