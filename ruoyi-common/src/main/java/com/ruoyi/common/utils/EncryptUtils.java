package com.ruoyi.common.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @description: 加密解密类
 * @author: SML
 * @date 2018/5/4 8:35
 */
public class EncryptUtils {
    //des加密密钥
    private final static String desPassword = "gK19ck41";

    /**
     * 银行卡加密
     *
     * @param account  卡号
     * @param password 付款记录Id
     * @return
     */
    public static String encBankAccount(String account, String password) {
        return desEncryption(account, password + desPassword);
    }

    /***
     * 银行卡卡解密
     * @param account 加密后的卡号
     * @param password 付款记录Id
     * @return
     */
    public static String decBankAccount(String account, String password) {
        return desDecryption(account, password + desPassword);
    }

    /**
     * @param src:
     * @Description: DES加密 默认密钥
     * @return: java.lang.String
     */
    public static String desEncryption(String src) {
        try {
            byte[] bytes = desEncryption(src.getBytes(Charset.forName("utf-8")), desPassword);
            return byte2hex(bytes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * @param src:
     * @Description: DES解密 默认密钥
     * @return: java.lang.String
     */
    public static String desDecryption(String src) {
        try {
            byte[] bytes = desDecryption(hex2byte(src), desPassword);
            return new String(bytes, Charset.forName("utf-8"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    /**
     * 加密方法
     *
     * @param src      加密前数据源
     * @param password 密钥
     * @return
     * @return String
     * @date 2019-09-30 10:47:10
     */
    public static String desEncryption(String src, String password) {
        try {
            byte[] bytes = desEncryption(src.getBytes(Charset.forName("utf-8")), password);
            return byte2hex(bytes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * 解密方法
     *
     * @param src      加密后的数据源
     * @param password 密钥
     * @return
     * @return String
     * @date 2019-09-30 10:48:01
     */
    public static String desDecryption(String src, String password) {
        try {
            byte[] bytes = desDecryption(hex2byte(src), password);
            return new String(bytes, Charset.forName("utf-8"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static byte[] desDecryption(byte[] src, String password) throws Exception {
        // DES算法要求有一个可信任的随机数源
        SecureRandom random = new SecureRandom();
        // 创建一个DESKeySpec对象
        DESKeySpec desKey = new DESKeySpec(password.getBytes());
        // 创建一个密匙工厂
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        // 将DESKeySpec对象转换成SecretKey对象
        SecretKey securekey = keyFactory.generateSecret(desKey);
        // Cipher对象实际完成解密操作
        Cipher cipher = Cipher.getInstance("DES");
        // 用密匙初始化Cipher对象
        cipher.init(Cipher.DECRYPT_MODE, securekey, random);
        // 真正开始解密操作
        return cipher.doFinal(src);
    }

    private static byte[] desEncryption(byte[] datasource, String password) {
        try {
            SecureRandom random = new SecureRandom();
            DESKeySpec desKey = new DESKeySpec(password.getBytes());
            //创建一个密匙工厂，然后用它把DESKeySpec转换成
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey securekey = keyFactory.generateSecret(desKey);
            //Cipher对象实际完成加密操作
            Cipher cipher = Cipher.getInstance("DES");
            //用密匙初始化Cipher对象
            cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
            //现在，获取数据并加密
            //正式执行加密操作
            return cipher.doFinal(datasource);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @param strhex:
     * @Description: 十六进制字符串转化为2进制
     * @return: byte[]
     */
    public static byte[] hex2byte(String strhex) {
        if (strhex == null) {
            return null;
        }
        int l = strhex.length();
        if (l % 2 == 1) {
            return null;
        }
        byte[] b = new byte[l / 2];
        for (int i = 0; i != l / 2; i++) {
            b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2), 16);
        }
        return b;
    }

    /**
     * @param b:
     * @Description: 将二进制转化为16进制字符串
     * @return: java.lang.String
     */
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs.toUpperCase();
    }

    // 修改加密和解密方法
    public static String desEncryption_new(String src, String pwd) {
        try {
            byte[] bytes = desEncryption(src.getBytes(Charset.forName("utf-8")), pwd);
            return byte2base64(bytes); // 使用Base64编码
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static String desDecryption_new(String src, String pwd) {
        try {
            byte[] bytes = desDecryption(base642byte(src), pwd); // 使用Base64解码
            return new String(bytes, Charset.forName("utf-8"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * @param b:
     * @Description: 将二进制转化为Base64字符串
     * @return: java.lang.String
     */
    public static String byte2base64(byte[] b) {
        return Base64.getEncoder().encodeToString(b);
    }

    /**
     * @param strBase64:
     * @Description: Base64字符串转化为二进制
     * @return: byte[]
     */
    public static byte[] base642byte(String strBase64) {
        return Base64.getDecoder().decode(strBase64);
    }

}
