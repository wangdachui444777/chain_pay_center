package com.ruoyi.quartz.task;

import com.ruoyi.bc.service.TokenPriceUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 价格更新定时任务
 */
@Component("tokenPriceUpdateTask")
public class TokenPriceUpdateTask {
    private static final Logger log = LoggerFactory.getLogger(TokenPriceUpdateTask.class);

    @Autowired
    private TokenPriceUpdateService priceUpdateService;
    /**
     * 定时更新价格（默认每5分钟）
     */

    public void updatePrices() {
        try {
            log.info("定时任务：开始更新代币价格");

            Map<String, Boolean> results = priceUpdateService.updateAllPrices();

            long successCount = results.values().stream().filter(Boolean::booleanValue).count();
            long totalCount = results.size();

            log.info("价格更新任务完成: {}/{} 成功", successCount, totalCount);

        } catch (Exception e) {
            log.error("价格更新任务异常", e);
        }
    }
}
