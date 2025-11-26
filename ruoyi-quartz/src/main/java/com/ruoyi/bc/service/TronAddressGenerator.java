package com.ruoyi.bc.service;


import com.ruoyi.bc.domain.BlockchainAddress;
import org.web3j.crypto.*;
import org.web3j.utils.Numeric;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import org.bitcoinj.core.Base58;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;

public class TronAddressGenerator {

    /**
     * 从私钥 hex 生成 TRON 地址（主网）
     */
    public static BlockchainAddress fromPrivateKey(String privateKeyHex) {
        if (privateKeyHex.startsWith("0x") || privateKeyHex.startsWith("0X")) {
            privateKeyHex = privateKeyHex.substring(2);
        }
        // pad private key to 64 hex chars
        String pk = Numeric.toHexStringNoPrefixZeroPadded(new BigInteger(privateKeyHex, 16), 64);

        // 1. 用 web3j 生成 ECKeyPair，获取 public key (BigInteger)
        ECKeyPair ecKeyPair = ECKeyPair.create(Numeric.hexStringToByteArray(pk));
        BigInteger publicKeyBI = ecKeyPair.getPublicKey();

        // 2. 把 publicKey 转为 64 字节数组 (uncompressed x||y)
        byte[] pubBytes = Numeric.toBytesPadded(publicKeyBI, 64);

        // 3. keccak256(pubBytes) -> 32 bytes
        Keccak.Digest256 keccak = new Keccak.Digest256();
        byte[] hashed = keccak.digest(pubBytes);

        // 4. 取最后 20 字节
        byte[] addr20 = Arrays.copyOfRange(hashed, 12, 32);

        // 5. 前面加 TRON 版本字节 0x41
        byte version = (byte) 0x41;
        byte[] addressBytes = new byte[1 + addr20.length];
        addressBytes[0] = version;
        System.arraycopy(addr20, 0, addressBytes, 1, addr20.length);

        // 6. Base58Check 编码（bitcoinj 提供 encodeChecked）
        //    Base58.encodeChecked(int version, byte[] payload) 会在内部计算 checksum
        //    但 bitcoinj 的 encodeChecked 期望的是 (version, payload) – version 是一个 int (0-255)
        String tronAddress = Base58.encodeChecked((int) (version & 0xFF), addr20);
        // 注意： bitcoinj 的 encodeChecked 会把 version字节拼在payload前并计算checksum，因此传入 payload=addr20，version=0x41 即可。

        return new BlockchainAddress("TRX", tronAddress, pk);
    }


    /**
     * 从助记词派生 TRON 地址（BIP44 路径 m/44'/195'/0'/0/{index}）
     */
    public static BlockchainAddress fromMnemonic(String mnemonic, String passphrase, int index) {
        byte[] seed = MnemonicUtils.generateSeed(mnemonic, passphrase == null ? "" : passphrase);
        Bip32ECKeyPair master = Bip32ECKeyPair.generateKeyPair(seed);
        final int[] path = {44 | 0x80000000, 195 | 0x80000000, 0 | 0x80000000, 0, index};
        Bip32ECKeyPair derived = Bip32ECKeyPair.deriveKeyPair(master, path);
        String privateKeyHex = Numeric.toHexStringNoPrefixZeroPadded(derived.getPrivateKey(), 64);
        return fromPrivateKey(privateKeyHex);
    }
}
