package com.ruoyi.bc.service;


import com.ruoyi.bc.domain.TokenPriceData;
import com.ruoyi.blockchain.domain.TokenPrices;

import java.util.List;

/**
 * 价格源适配器接口
 */
public interface IPriceSourceAdapter {

    /**
     * 获取价格源名称
     */
    String getSourceName();

    /**
     * 批量获取代币价格
     * @param tokenSymbols 代币符号列表
     * @return 价格数据列表
     */
    List<TokenPriceData> fetchPrices(List<String> tokenSymbols);

    /**
     * 获取单个代币价格
     * @param tokenSymbol 代币符号
     * @return 价格数据
     */
    TokenPriceData fetchPrice(String tokenSymbol);
    /**
     * 检查是否支持该代币
     */
    boolean isSupported(String tokenSymbol);
}