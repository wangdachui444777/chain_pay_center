package com.ruoyi.bc.service;


import com.ruoyi.bc.domain.BlockchainAddress;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.MnemonicUtils;
import org.web3j.utils.Numeric;

import java.security.SecureRandom;

public class AddressGeneratorFactory {

    public static BlockchainAddress generateFromPrivateKey(String chain, String privateKeyHex) {
        chain = chain == null ? "" : chain.trim().toUpperCase();
        switch (chain) {
            case "TRX":
                return TronAddressGenerator.fromPrivateKey(privateKeyHex);
            case "ETH":
                return EthereumAddressGenerator.fromPrivateKey(privateKeyHex);
            default:
                throw new IllegalArgumentException("Unsupported chain: " + chain);
        }
    }

    public static BlockchainAddress generateFromMnemonic(String chain, String mnemonic,int index) {
        chain = chain == null ? "" : chain.trim().toUpperCase();
        String passphrase="";
        switch (chain) {
            case "TRX":
                return TronAddressGenerator.fromMnemonic(mnemonic, passphrase, index);
            case "ETH":
                return EthereumAddressGenerator.fromMnemonic(mnemonic, passphrase, index);
            default:
                throw new IllegalArgumentException("Unsupported chain: " + chain);
        }
    }

    // convenience random generation using web3j
    public static BlockchainAddress generateRandom(String chain) throws Exception {
        // create random ECKeyPair
        ECKeyPair keyPair = Keys.createEcKeyPair();
        String pk = Numeric.toHexStringNoPrefixZeroPadded(keyPair.getPrivateKey(), 64);
        return generateFromPrivateKey(chain, pk);
    }
    /**
     * 获取12个词
     * @return
     */
    public static String getMnemonic(){
        SecureRandom SECURE_RANDOM = new SecureRandom();
        byte[] initialEntropy = new byte[16];// 16位 12个词，32位24个词
        SECURE_RANDOM.nextBytes(initialEntropy);
        return MnemonicUtils.generateMnemonic(initialEntropy);
    }
    // ========== 示例使用 ==========

    public static void main(String[] args) {
        System.out.println("\n");

        // 示例私钥（仅用于测试）
        String testPrivateKey = "deab0fe29b1f1fca480c96f68dfcd162674775a050d27150a32ea6a62010de01";
//deab0fe29b1f1fca480c96f68dfcd162674775a050d27150a32ea6a62010de01
        System.out.println("=== 地址生成示例 ===\n");

        // Ethereum
        System.out.println("Ethereum Address:");
        System.out.println(generateFromPrivateKey("ETH",testPrivateKey));

        // TRON
        System.out.println("\nTRON Address:");
        System.out.println(generateFromPrivateKey("TRX",testPrivateKey));
/**
 SecureRandom SECURE_RANDOM = new SecureRandom();
 byte[] initialEntropy = new byte[32];
 SECURE_RANDOM.nextBytes(initialEntropy);
 String mnemonic = MnemonicUtils.generateMnemonic(initialEntropy);**/

        // 从助记词生成示例
        System.out.println("=== 助记词示例 ===\n");
        String testMnemonic = "cluster tumble tiny easy dose leaf stand two lift faith echo endorse";

        System.out.println("TRON Address:");
        System.out.println(generateFromMnemonic("trx",testMnemonic,0));

        System.out.println("\nEthereum Address:");
        System.out.println(generateFromMnemonic("eth",testMnemonic,0));




    }
}

