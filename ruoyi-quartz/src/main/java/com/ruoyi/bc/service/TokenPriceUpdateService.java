package com.ruoyi.bc.service;


import com.ruoyi.bc.domain.TokenPriceData;
import com.ruoyi.blockchain.domain.TokenPrices;
import com.ruoyi.blockchain.service.ITokenPricesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 代币价格更新服务
 */
@Service
public class TokenPriceUpdateService {

    private static final Logger log = LoggerFactory.getLogger(TokenPriceUpdateService.class);

    @Autowired
    private ITokenPricesService iTokenPricesService;

    @Autowired
    private Map<String, IPriceSourceAdapter> priceAdapters;

    /** 价格源优先级列表 */
    private List<String> sourcePriority;

    @PostConstruct
    public void init() {
        // 构建价格源优先级
        sourcePriority = new ArrayList<>();
        sourcePriority.add("binance");
        sourcePriority.add("okx");
        log.info("========================================");
        log.info("价格更新服务初始化完成");
        log.info("价格源优先级: {}", sourcePriority);
        log.info("可用适配器: {}", priceAdapters.keySet());
        log.info("========================================");
    }
    /**
     * 更新所有代币价格
     */
    public Map<String, Boolean> updateAllPrices() {

        //log.info("开始更新代币价格...");
        Map<String, Boolean> results = new HashMap<>();
        try {
            // 获取所有需要更新的代币（排除稳定币 USDT/USDC）
            TokenPrices wprices=new TokenPrices();
            wprices.setEnabled(1);
            List<TokenPrices> tokenPricesList = iTokenPricesService.selectTokenPricesList(wprices);
            // 过滤掉稳定币
            // 获取所有需要更新的代币（排除稳定币 USDT/USDC）
            List<String> tokenSymbols = tokenPricesList.stream()
                    .filter(t -> !isStableCoin(t.getTokenSymbol()))
                    .map(TokenPrices::getTokenSymbol)
                    .distinct()
                    .collect(Collectors.toList());

            //log.info("需要更新 {} 个代币: {}", tokenSymbols.size(), tokenSymbols);

            // 尝试从价格源获取价格
            Map<String, TokenPriceData> priceDataMap = fetchPricesWithFallback(tokenSymbols);

            // 更新到数据库
            int successCount = 0;
            int failCount = 0;

            for (Map.Entry<String, TokenPriceData> entry : priceDataMap.entrySet()) {
                String tokenSymbol = entry.getKey();
                TokenPriceData priceData = entry.getValue();

                if (priceData.getSuccess() && priceData.getPriceInUsdt() != null) {
                    boolean updated = updateTokenPrice(tokenSymbol, priceData);
                    results.put(tokenSymbol, updated);

                    if (updated) {
                        successCount++;
                    } else {
                        failCount++;
                    }
                } else {
                    log.warn("{} 价格获取失败: {}", tokenSymbol, priceData.getErrorMessage());
                    results.put(tokenSymbol, false);
                    failCount++;
                }
            }

            log.info("价格更新完成: 成功 {}, 失败 {}", successCount, failCount);

        } catch (Exception e) {
            log.error("更新价格异常", e);
        }

        return results;
    }
    /**
     * 更新单个代币价格
     */
    public boolean updateTokenPrice(String tokenSymbol, TokenPriceData priceData) {
        try {
            // 查询该代币的所有链配置

            // 更新所有链的价格
                boolean updated = iTokenPricesService.updatePrice(
                        tokenSymbol,
                        priceData.getPriceInUsdt()
                );

                if (updated) {
                    log.info("{}  价格已更新: ${} (来源: {})",
                            tokenSymbol,
                            priceData.getPriceInUsdt(),
                            priceData.getSource());
                }

            return true;

        } catch (Exception e) {
            log.error(" 更新 {} 价格失败", tokenSymbol, e);
            return false;
        }
    }

    /**
     * 从价格源获取价格（支持降级）
     */
    private Map<String, TokenPriceData> fetchPricesWithFallback(List<String> tokenSymbols) {
        Map<String, TokenPriceData> resultMap = new HashMap<>();
        Set<String> remainingTokens = new HashSet<>(tokenSymbols);

        // 按优先级尝试价格源
        for (String sourceName : sourcePriority) {
            if (remainingTokens.isEmpty()) {
                break;
            }

            String adapterName = sourceName + "PriceAdapter";
            IPriceSourceAdapter adapter = priceAdapters.get(adapterName);

            if (adapter == null) {
                log.warn("未找到价格源适配器: {}", sourceName);
                continue;
            }

           // log.info("📡 尝试从 {} 获取价格...", adapter.getSourceName());

            try {
                // 只查询该源支持的代币
                List<String> supportedTokens = remainingTokens.stream()
                        .filter(adapter::isSupported)
                        .collect(Collectors.toList());

                if (supportedTokens.isEmpty()) {
                    continue;
                }

                List<TokenPriceData> prices = adapter.fetchPrices(supportedTokens);

                for (TokenPriceData priceData : prices) {
                    if (priceData.getSuccess()) {
                        resultMap.put(priceData.getTokenSymbol(), priceData);
                        remainingTokens.remove(priceData.getTokenSymbol());
                    }
                }

            } catch (Exception e) {
                log.error("{} 获取价格失败，尝试下一个源", adapter.getSourceName(), e);
            }
        }

        // 记录未能获取价格的代币
        if (!remainingTokens.isEmpty()) {
            log.warn("⚠️ 以下代币未能获取价格: {}", remainingTokens);
            for (String token : remainingTokens) {
                resultMap.put(token, TokenPriceData.failure(token, "所有价格源均失败"));
            }
        }

        return resultMap;
    }

    /**
     * 判断是否为稳定币
     */
    private boolean isStableCoin(String tokenSymbol) {
        if (tokenSymbol == null) return false;
        String upper = tokenSymbol.toUpperCase();
        return "USDT".equals(tokenSymbol) || "USDC".equals(tokenSymbol);
    }
}