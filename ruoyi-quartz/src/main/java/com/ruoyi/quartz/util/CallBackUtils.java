package com.ruoyi.quartz.util;

import com.ruoyi.blockchain.domain.BcWithdrawRecord;
import com.ruoyi.blockchain.domain.Platforms;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.block.BcSignatureUtils;
import com.ruoyi.common.utils.http.HttpUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class CallBackUtils {

    public static Map<String, Object> buildCommonData(
            Long txId,
            String txHash,
            String txStatus,
            String chainType,
            String tokenSymbol,
            String tokenContract,
            BigDecimal amount,
            String fromAddress,
            String toAddress,
            Long confirmations,
            Date confirmedTime
    ) {
        long now = System.currentTimeMillis() / 1000;
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("txId", txId);
        data.put("txHash", txHash);
        data.put("txStatus", txStatus);
        data.put("chainType", chainType);
        data.put("tokenSymbol", tokenSymbol);
        data.put("tokenContract", tokenContract);
        data.put("amount", amount.toPlainString());
        data.put("fromAddress", fromAddress);
        data.put("toAddress", toAddress);
        data.put("confirmations", confirmations);
        if (confirmedTime!=null){
            data.put("confirmedTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, confirmedTime));
        }else{
            data.put("confirmedTime", DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS,DateUtils.getNowDate()));
        }
        data.put("timestamp", now);

        return data;
    }

    /**
     * 执行HTTP回调通知
     */
    public static boolean executeCallback( Map<String, Object> callbackData, Platforms platform,String callBackUrl) {
        try {
            // 构建回调参数
            //Map<String, Object> callbackData = buildCallbackData(record);
            // 生成签名
            String sign = BcSignatureUtils.generateSign(callbackData, platform.getSecretKey());
            callbackData.put("sign", sign);

            // 发送HTTP请求
            String body= HttpUtils.sendPost(callBackUrl,callbackData);

            //log.info("发送回调请求，URL:{}, 数据:{}", platform.getWithdrawUrl(), callbackData);
            if ("SUCCESS".equalsIgnoreCase(body.trim())) {
              //  log.info("回调成功，响应内容：SUCCESS");
                return true;
            } else {
              //  log.warn("回调失败，响应内容不是SUCCESS：{}", body);
                return false;
            }

        } catch (Exception e) {
            //log.error("执行回调请求异常", e);
            return false;
        }
    }
}
