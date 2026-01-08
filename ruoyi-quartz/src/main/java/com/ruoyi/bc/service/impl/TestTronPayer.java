package com.ruoyi.bc.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.tron.trident.abi.FunctionEncoder;
import org.tron.trident.abi.datatypes.Address;
import org.tron.trident.abi.datatypes.Function;
import org.tron.trident.abi.datatypes.generated.Uint256;
import org.tron.trident.core.ApiWrapper;
import org.tron.trident.core.key.KeyPair;
import org.tron.trident.proto.Chain;
import org.tron.trident.proto.Response;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

/**
 * 示例：A 先委派能量给 B（B 无 TRX 也可用），B 单签转 ERC20 到 C。
 * 节点不支持 gettransactionsign，全部本地签名。
 */
public class TestTronPayer {

    private static final String API_KEY = "5f87c17c-5829-40f7-a6bf-560f49975a95";

    private static final String A_ADDRESS = "TP6CqFPLbTdAuDxdhzoiFuBfM5WpMayFFM";
    private static final String A_PRIVATE_KEY = "740f93bdedffed9a23de9361c2fc15d1cc86c7f5f50db9e7e77b43791a504714"; // 代付/委派方私钥，占位

    private static final String B_ADDRESS = "TKzMkkC3AAMXAbmbBSacj2xWGE3yGRZxxn";
    private static final String B_PRIVATE_KEY = "04de9db2c400523906fb7298ce57c6cefc593da14fe0e4b264e3af6aed1568c4";
    private static final String C_ADDRESS = "TQYTHCTbepdTiMzV5h41eJEYrS7orhm7EX";

    private static final String USDT_CONTRACT = "TXYZopYRdj2D9XRtbG411XZZ3kM5VkAeBf";
    private static final long FEE_LIMIT = 100_000_000L;
    // 委派能量数量（sun），示例 30 TRX
    private static final long DELEGATE_AMOUNT = 30_000_000L;
    private static final long ACTIVATE_AMOUNT = 10L; // 0.00001 TRX 激活 B

    public static void main(String[] args) {
        if ("REPLACE_WITH_PAYER_PRIV".equals(A_PRIVATE_KEY)) {
            throw new IllegalStateException("请先设置 A_PRIVATE_KEY");
        }
        ApiWrapper wrapper = ApiWrapper.ofNile(""); // 测试链无需 API Key

        try {
            Response.PricesResponseMessage prices= wrapper.getEnergyPrices();

            System.out.println("prices ="+ prices.getPrices());
            //ensureAccountExists(wrapper, A_ADDRESS, A_PRIVATE_KEY, B_ADDRESS);
            //delegateEnergy(wrapper, A_ADDRESS, A_PRIVATE_KEY, B_ADDRESS, DELEGATE_AMOUNT);
            unDelegateEnergySafe(wrapper,A_ADDRESS,A_PRIVATE_KEY,B_ADDRESS,DELEGATE_AMOUNT);
            //String txid = transferUsdt(wrapper, B_ADDRESS, B_PRIVATE_KEY, C_ADDRESS, new BigInteger("4000000"));
            //System.out.println("USDT txid = " + txid);
        } finally {
            wrapper.close();
        }
    }


    /**
     * 正常好几年都不会变
     * 获取能量价格，
     * @param api
     * @return 最新的能量价格
     */
    public static long getCurrentPriceCached(ApiWrapper api) {
        String p = api.getEnergyPrices().getPrices();
        String[] arr = p.split(",");

        String[] kv = arr[arr.length - 1].split(":");
        long ts = Long.parseLong(kv[0]);
        long price = Long.parseLong(kv[1]);
        return price;
    }
    //能量换算成trx
    public static double energyToTrx(long energy, ApiWrapper api) {
        long price = getCurrentPriceCached(api);
        return energy * price / 1_000_000.0;
    }
    /**
     * 若 B 未激活，由 A 先转少量 TRX 激活。
     */
    private static void ensureAccountExists(ApiWrapper wrapper, String activator, String activatorPrv, String target) {
        try {
            Response.Account account = wrapper.getAccount(target);
            if (account == null || account.getAddress().isEmpty()) {
                Response.TransactionExtention ext = wrapper.transfer(activator, target, ACTIVATE_AMOUNT);
                Chain.Transaction signed = wrapper.signTransaction(ext, new KeyPair(activatorPrv));
                String res = wrapper.broadcastTransaction(signed);
                System.out.println("activate B broadcast=" +res);
                // 激活交易需要上链确认后再继续委派/转账，实际使用可在此等待确认
            }
        } catch (Exception e) {
            throw new RuntimeException("激活账户失败: " + target, e);
        }
    }

    /**
     * A 委派能量给 B（Stake 2.0），无需 B 余额。
     */
    private static void delegateEnergy(ApiWrapper wrapper, String from, String prvFrom, String to, long amountSun) {
        try {
            Response.TransactionExtention ext = wrapper.delegateResourceV2(
                    from,
                    amountSun,
                    1,          // 1 表示 ENERGY
                    to,
                    false,      // 不锁定
                    0           // expireTime 0 表示默认
            );
            Chain.Transaction signed = wrapper.signTransaction(ext, new KeyPair(prvFrom));
            String res = wrapper.broadcastTransaction(signed);
            System.out.println("delegateEnergy broadcast=" + res);
        } catch (Exception e) {
            throw new RuntimeException("delegateEnergy failed", e);
        }
    }
    /**
     * 释放 A → B 的委托能量（Stake 2.0）
     */
    public static void unDelegateEnergySafe(ApiWrapper api,
                                            String from,
                                            String prvFrom,
                                            String to,
                                            long amount) {
        try {

            // sanity 校验
            if (amount <= 0) {
                throw new IllegalArgumentException("undelegate amount must > 0");
            }


            // 构建交易
            Response.TransactionExtention ext =
                    api.undelegateResource(from, amount, 1, to);

            if (!ext.getResult().getResult()) {
                throw new RuntimeException("unDelegate failed: "
                        + ext.getResult().getCode() + ","
                        + ext.getResult().getMessage().toStringUtf8());
            }

            // 签名 + 广播
            Chain.Transaction signed = api.signTransaction(ext, new KeyPair(prvFrom));
            String txid = api.broadcastTransaction(signed);

            System.out.println("[OK] unDelegate success, txid=" + txid);

        } catch (Exception e) {
            throw new RuntimeException("unDelegateEnergy failed", e);
        }
    }

    /**
     * B 使用委派能量单签转 USDT 到 C。
     */
    private static String transferUsdt(ApiWrapper wrapper, String from, String prvFrom, String to, BigInteger amount) {
        try {
            Function function = new Function(
                    "transfer",
                    Arrays.asList(new Address(to), new Uint256(amount)),
                    Collections.emptyList()
            );
            Response.EstimateEnergyMessage e = wrapper.estimateEnergy(from, USDT_CONTRACT, function);

            System.out.println("需要能量：" + e.getEnergyRequired());
            String data = FunctionEncoder.encode(function);

            Response.TransactionExtention ext = wrapper.triggerContract(
                    from,
                    USDT_CONTRACT,
                    data,
                    0L,
                    0L,
                    null,
                    FEE_LIMIT
            );
            Chain.Transaction signed = wrapper.signTransaction(ext, new KeyPair(prvFrom));
            String res = wrapper.broadcastTransaction(signed);
           // JSONObject broadcast = JSONObject.parseObject(res);
            System.out.println("transfer broadcast=" + res);
            return res;
        } catch (Exception e) {
            throw new RuntimeException("transferUsdt failed", e);
        }
    }
}
