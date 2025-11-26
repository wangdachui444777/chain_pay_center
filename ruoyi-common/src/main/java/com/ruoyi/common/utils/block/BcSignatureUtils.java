package com.ruoyi.common.utils.block;


import com.ruoyi.common.utils.security.Md5Utils;

import java.util.*;

public class BcSignatureUtils {

    /**
     * 验证签名是否合法
     *
     * @param params     参与签名的参数（不包含 sign）
     * @param sign       平台提交的签名
     * @param secretKey  平台密钥
     * @param maxDiffSec 允许的最大时间差（秒）
     * @throws SecurityException 当签名错误或时间过期
     */
    public static void verifySignature(Map<String, Object> params, String sign, String secretKey, long maxDiffSec) {
        if (params == null || sign == null || secretKey == null) {
            throw new SecurityException("Missing required parameters");
        }

        String platformId =String.valueOf(params.get("platformId"));
        String timestamp = String.valueOf(params.get("timestamp"));

        if (platformId == null || timestamp == null) {
            throw new SecurityException("Missing required headers: platformId or timestamp");
        }

        // 检查时间戳格式
        long ts;
        try {
            ts = Long.parseLong(timestamp);
        } catch (NumberFormatException e) {
            throw new SecurityException("Invalid timestamp format");
        }

        // 检查是否超时
        long now = System.currentTimeMillis() / 1000;
        if (Math.abs(now - ts) > maxDiffSec) {
            throw new SecurityException("Request expired");
        }

        // 构建签名内容
        String expectedSign = generateSign(params,secretKey);
        System.out.println("sign："+expectedSign);
        if (!expectedSign.equals(sign)) {
            throw new SecurityException("Invalid Signature");
        }
    }
    /**
     * 生成签名
     */
    public static String generateSign(Map<String, Object> data, String secretKey) {
        // 构建签名内容
        String content = buildSignContent(data);
        //  MD5加密
        String signStr = Md5Utils.hash(content + secretKey).toUpperCase();
        return signStr;
    }

    /**
     * 构建签名原始字符串（按 key 排序后拼接）
     *
     * ⚙️ 示例：
     * params = { platformId=abc, timestamp=1234567890, amount=100, toAddress=Txxxx }
     * content = "100Txxxxabc1234567890"
     *
     * ⚠️ 注意：
     * - 签名内容是所有参数值的拼接，不包含 sign
     * - 参数名按字典序升序排列，确保双方顺序一致
     */
    public static String buildSignContent(Map<String, Object> params) {
        /** return params.entrySet().stream()
                .filter(e -> StringUtils.isNotEmpty(String.valueOf(e.getValue())) && !"sign".equalsIgnoreCase(e.getKey()))
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .collect(Collectors.joining());**/
        // 1. 参数排序
        TreeMap<String, Object> sortedData = new TreeMap<>(params);

        // 2. 拼接字符串
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : sortedData.entrySet()) {
            if (entry.getValue() != null && !"sign".equals(entry.getKey())) {
                sb.append(entry.getValue());
            }
        }
       return sb.toString();
    }

    /**
     * 生成签名（供平台端调用）
     *
     * @param params 参数
     * @param secretKey 平台密钥
     * @return MD5签名（大写）
     */
    public static String generateSignature(Map<String, Object> params, String secretKey) {
        String content = buildSignContent(params);
        return Md5Utils.hash(content + secretKey).toUpperCase();
    }
}

