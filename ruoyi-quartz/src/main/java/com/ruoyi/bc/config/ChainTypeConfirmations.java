package com.ruoyi.bc.config;

public class ChainTypeConfirmations {

    public static final String CHAIN_ARETH="ARETH";
    public static final String CHAIN_AVAX="AVAX";
    public static final String CHAIN_BTC="BTC";
    public static final String CHAIN_ETH="ETH";
    public static final String CHAIN_FTM="FTM";
    public static final String CHAIN_OETH="OETH";
    public static final String CHAIN_MATIC="MATIC";
    public static final String CHAIN_BNB="BNB";
    public static final String CHAIN_TRX="TRX";
    /**
     * 获取所需确认数
     */
    public static int getRequiredConfirmations(String chainType) {
        switch (chainType.toUpperCase()) {
            case "TRX":
            case "TRON":
                return 19; // TRON 19个确认
            case "ETH":
            case "ETHEREUM":
                return 12; // ETH 12个确认
            case "BSC":
                return 15; // BSC 15个确认
            case "BTC":
                return 6;  // BTC 6个确认
            default:
                return 12;
        }
    }
    public static boolean hasTestNetWork(String apiUrl) {
        if (apiUrl.contains("nile")) {
            return true;
        }
        return false;
    }

}
