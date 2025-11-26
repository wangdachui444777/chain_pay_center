package com.ruoyi.blockchain.mapper;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.blockchain.domain.TokenPrices;
import io.lettuce.core.dynamic.annotation.Param;

/**
 * 币种配置Mapper接口
 * 
 * @author dc
 * @date 2025-10-29
 */
public interface TokenPricesMapper 
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
    int updatePriceForSymbol(TokenPrices tokenPrices);
    /**
     * 删除币种配置
     * 
     * @param id 币种配置主键
     * @return 结果
     */
    public int deleteTokenPricesById(Long id);

    /**
     * 批量删除币种配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteTokenPricesByIds(String[] ids);
}
