package com.ruoyi.quartz.task;

import com.ruoyi.blockchain.domain.BcTransactions;
import com.ruoyi.blockchain.domain.Platforms;
import com.ruoyi.blockchain.service.IBcTransactionsService;
import com.ruoyi.blockchain.service.IPlatformsService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.block.BcSignatureUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.quartz.util.CallBackUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 区块链交易回调通知定时任务
 *
 * @author ruoyi
 */
@Component("transactionCallbackTask")
public class TransactionCallbackTask {

    private static final Logger log = LoggerFactory.getLogger(TransactionCallbackTask.class);

    @Autowired
    private IBcTransactionsService transactionsService;

    @Autowired
    private IPlatformsService platformsService;

    /**
     * 回调状态：待回调
     */
    private static final String CALLBACK_STATUS_PENDING = "1";

    /**
     * 回调状态：回调完成
     */
    private static final String CALLBACK_STATUS_SUCCESS = "2";

    /**
     * 回调状态：失败
     */
    private static final String CALLBACK_STATUS_FAILED = "3";

    /**
     * 交易已确认
     */
    private static final String CONFIRMED_YES = "1";

    /**
     * 超时时间：5分钟（毫秒）
     */
    private static final long TIMEOUT_MILLIS = 30 * 60 * 1000;

    /**
     * 重试间隔时间（秒）：第1次立即，第2次30秒，第3次60秒，第4次90秒，第5次120秒
     */
    private static final long[] RETRY_INTERVALS = {0, 30, 60, 90, 120};

    /**
     * 处理回调通知
     * cron表达式建议：0/10 * * * * ? （每10秒执行一次）
     */
    public void callBack() {
        log.info("开始执行区块链交易回调通知任务");
        try {
            // 1. 查询待回调的已确认交易
            BcTransactions query = new BcTransactions();
            query.setConfirmed(CONFIRMED_YES);
            query.setCallbackStatus(CALLBACK_STATUS_PENDING);
            List<BcTransactions> pendingTransactions = transactionsService.selectBcTransactionsList(query);

            if (pendingTransactions == null || pendingTransactions.isEmpty()) {
                log.info("暂无待回调的交易记录");
                return;
            }
            // 2. 遍历处理每笔交易
            for (BcTransactions transaction : pendingTransactions) {
                processTransaction(transaction);
            }

        } catch (Exception e) {
            log.error("执行回调通知任务异常", e);
        }

        log.info("区块链交易回调通知任务执行完成");
    }

    /**
     * 处理单笔交易回调
     */
    private void processTransaction(BcTransactions transaction) {
        try {
            Date now = DateUtils.getNowDate();
            Date detectedTime = transaction.getDetectedTime();

            if (detectedTime == null) {
                log.warn("交易ID:{} 检测时间为空，跳过处理", transaction.getId());
                return;
            }

            // 计算已经过去的时间
            long elapsedTime = now.getTime() - detectedTime.getTime();

            // 超过5分钟，标记为失败
            if (elapsedTime > TIMEOUT_MILLIS) {
                log.warn("交易ID:{} 回调超时，标记为失败。检测时间:{}, 当前时间:{}",
                        transaction.getId(), detectedTime, now);
                updateCallbackStatus(transaction.getId(), CALLBACK_STATUS_FAILED, null);
                return;
            }
            // 判断当前是否应该进行回调（根据重试次数和时间间隔）
            int retryCount = calculateRetryCount(transaction);

            if (retryCount >= RETRY_INTERVALS.length) {
                // 已达到最大重试次数，但未超时，继续等待
                log.debug("交易ID:{} 已达到最大重试次数，等待超时", transaction.getId());
                return;
            }
            // 检查是否到达下次重试时间
            long nextRetryTime = detectedTime.getTime() + (RETRY_INTERVALS[retryCount] * 1000);
            if (now.getTime() < nextRetryTime) {
                // 还未到重试时间
                return;
            }

            log.info("开始处理交易回调，交易ID:{}, 第{}次尝试", transaction.getId(), retryCount + 1);

            // 获取平台信息
            Platforms platform = platformsService.selectPlatformsById(transaction.getPlatformId());
            if (platform == null || StringUtils.isEmpty(platform.getCallbackUrl())) {
                log.error("交易ID:{} 平台信息不存在或回调地址为空", transaction.getId());
                updateCallbackStatus(transaction.getId(), CALLBACK_STATUS_FAILED, null);
                return;
            }
            // 构建回调参数
            Map<String, Object> callbackData = buildCallbackData(transaction);
            // 执行回调
            boolean success = CallBackUtils.executeCallback(callbackData, platform,platform.getCallbackUrl().trim());

            if (success) {
                // 回调成功
                log.info("交易ID:{} 回调成功", transaction.getId());
                updateCallbackStatus(transaction.getId(), CALLBACK_STATUS_SUCCESS, now);
            } else {
                // 回调失败，不更新状态，等待下次重试
                log.warn("交易ID:{} 回调失败，等待重试", transaction.getId());
            }

        } catch (Exception e) {
            log.error("处理交易ID:{} 回调异常", transaction.getId(), e);
        }
    }

    /**
     * 计算当前应该是第几次重试
     */
    private int calculateRetryCount(BcTransactions transaction) {
        Date detectedTime = transaction.getDetectedTime();
        Date now = new Date();
        long elapsedSeconds = (now.getTime() - detectedTime.getTime()) / 1000;

        // 根据已过去的时间判断应该进行第几次重试
        for (int i = RETRY_INTERVALS.length - 1; i >= 0; i--) {
            if (elapsedSeconds >= RETRY_INTERVALS[i]) {
                return i;
            }
        }
        return 0;
    }



    /**
     * 构建回调数据
     */
    private Map<String, Object> buildCallbackData(BcTransactions transaction) {

        Map<String, Object> data = CallBackUtils.buildCommonData(
                transaction.getId(),
                transaction.getTxHash(),
                transaction.getTxStatus(),
                transaction.getChainType(),
                transaction.getTokenSymbol(),
                transaction.getTokenContract(),
                transaction.getAmount(),
                transaction.getFromAddress(),
                transaction.getToAddress(),
                transaction.getConfirmations(),
                transaction.getConfirmedTime()
        );

        // 特殊字段
        data.put("direction", transaction.getDirection());
        data.put("blockNumber", transaction.getBlockNumber());

        return data;
    }



    /**
     * 更新回调状态
     */
    private void updateCallbackStatus(Long transactionId, String callbackStatus, Date callbackTime) {
        BcTransactions update = new BcTransactions();
        update.setId(transactionId);
        update.setCallbackStatus(callbackStatus);
        update.setCallbackTime(callbackTime);
        transactionsService.updateBcTransactions(update);
    }
}
