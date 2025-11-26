package com.ruoyi.blockchain.service;

import java.util.List;
import com.ruoyi.blockchain.domain.TokenPrices;

/**
 * 币种配置Service接口
 * 
 * @author dc
 * @date 2025-10-29
 */
public interface ITokenPricesService 
{
    /**
     * 查询币种配置
     * 
     * @param id 币种配置主键
     * @return 币种配置
     */
    public TokenPrices selectTokenPricesById(Long id);

    /**
     * 查询币种配置列表
     * 
     * @param tokenPrices 币种配置
     * @return 币种配置集合
     */
    public List<TokenPrices> selectTokenPricesList(TokenPrices tokenPrices);

    /**
     *查询需要监听的币种
     * @param chainType
     * @return
     */
    public List<TokenPrices> getEnabledTokens(String chainType);

    /**
     * 获取代币配置（按符号）
     */
    TokenPrices getTokenBySymbol(String chainType, String tokenSymbol);

    /**
     * 初始化代币配置缓存
     */
    public void initTokenConfigCache(String chainType);

    /**
     * 获取主币缓存配置
     * @param chainType
     * @param tokenSymbol
     * @return
     */
    public TokenPrices getTokenCacheByChain(String chainType,String tokenSymbol);

    /**
     * 根据合约地址获取缓存
     * @param contractAddress
     * @return
     */
    public TokenPrices getTokenCacheByContractAddress(String contractAddress);
    /**
     * 新增币种配置
     * 
     * @param tokenPrices 币种配置
     * @return 结果
     */
    public int insertTokenPrices(TokenPrices tokenPrices);

    /**
     * 修改币种配置
     * 
     * @param tokenPrices 币种配置
     * @return 结果
     */
    public int updateTokenPrices(TokenPrices tokenPrices);

    /**
     * 更新代币价格
     */
    boolean updatePrice(String tokenSymbol, java.math.BigDecimal price);
    /**
     * 获取代币配置（按符号）
     */

    /**
     * 批量删除币种配置
     * 
     * @param ids 需要删除的币种配置主键集合
     * @return 结果
     */
    public int deleteTokenPricesByIds(String ids);

    /**
     * 删除币种配置信息
     * 
     * @param id 币种配置主键
     * @return 结果
     */
    public int deleteTokenPricesById(Long id);
}
