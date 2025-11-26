package com.ruoyi.bc.service;

import com.ruoyi.bc.domain.BlockchainAddress;
import org.web3j.crypto.*;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

public class EthereumAddressGenerator {

    /**
     * 从私钥（hex）生成 ETH 地址
     */
    public static BlockchainAddress fromPrivateKey(String privateKeyHex) {
        if (privateKeyHex.startsWith("0x") || privateKeyHex.startsWith("0X")) {
            privateKeyHex = privateKeyHex.substring(2);
        }
        // pad to 64 hex chars
        String pk = Numeric.toHexStringNoPrefixZeroPadded(new BigInteger(privateKeyHex, 16), 64);
        Credentials credentials = Credentials.create(pk);
        String address = credentials.getAddress(); // 包含 0x 前缀
        return new BlockchainAddress("ETH", address, pk);
    }

    /**
     * 从助记词 (BIP39) 派生 ETH 私钥/地址
     * 使用路径 m/44'/60'/0'/0/{index}
     */
    public static BlockchainAddress fromMnemonic(String mnemonic, String passphrase, int index) {
        // 1. 生成 seed
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, passphrase == null ? "" : passphrase);
        // 2. bip32 master
        Bip32ECKeyPair master = Bip32ECKeyPair.generateKeyPair(seed);
        // 3. derivation path
        final int[] path = {44 | 0x80000000, 60 | 0x80000000, 0 | 0x80000000, 0, index};
        Bip32ECKeyPair derived = Bip32ECKeyPair.deriveKeyPair(master, path);
        String privateKeyHex = Numeric.toHexStringNoPrefixZeroPadded(derived.getPrivateKey(), 64);
        return fromPrivateKey(privateKeyHex);
    }
}
