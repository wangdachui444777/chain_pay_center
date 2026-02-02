package com.ruoyi.blockchain.service.impl;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.ruoyi.blockchain.mapper.TokenPricesMapper;
import com.ruoyi.blockchain.domain.TokenPrices;
import com.ruoyi.blockchain.service.ITokenPricesService;
import com.ruoyi.common.core.text.Convert;

/**
 * 币种配置Service业务层处理
 * 
 * @author dc
 * @date 2025-10-29
 */
@Service
public class TokenPricesServiceImpl implements ITokenPricesService 
{
    @Autowired
    private TokenPricesMapper tokenPricesMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisCache redisCache;


    /** 币种配置 */
    public static final String TOKEN_PREFIX = "bc:token:";
    /** 链参数缓存 */
    public static final String CHAIN_PARAM_PREFIX = "bc:chainparam:";
    /**
     * 查询币种配置
     * 
     * @param id 币种配置主键
     * @return 币种配置
     */
    @Override
    public TokenPrices selectTokenPricesById(Long id)
    {
        return tokenPricesMapper.selectTokenPricesById(id);
    }

    /**
     * 查询币种配置列表
     * 
     * @param tokenPrices 币种配置
     * @return 币种配置
     */
    @Override
    public List<TokenPrices> selectTokenPricesList(TokenPrices tokenPrices)
    {
        return tokenPricesMapper.selectTokenPricesList(tokenPrices);
    }

    public TokenPrices getTokenBySymbol(String chainType, String tokenSymbol){
        TokenPrices w=new TokenPrices();
       // PageHelper.offsetPage(0,1);
        w.setChainType(chainType.toUpperCase());
        w.setTokenSymbol(tokenSymbol.toUpperCase());
       List<TokenPrices> tokenPricesList= this.selectTokenPricesList(w);
       if (tokenPricesList.size()>0){
           return tokenPricesList.get(0);
       }
       return null;
    }

    /**
     * 初始化代币配置缓存
     */
    public void initTokenConfigCache(String chainType) {
        List<TokenPrices> tokens = this.getEnabledTokens(chainType);
        for (TokenPrices token : tokens) {
            setCache(token);
        }
    }

    /**
     * 获取主币缓存配置
     * @param chainType
     * @param tokenSymbol
     * @return
     */
    public TokenPrices getTokenCacheByChain(String chainType,String tokenSymbol){
        String key=TOKEN_PREFIX+(chainType+":"+tokenSymbol).toUpperCase();
        return redisCache.getCacheObject(key);
    }

    /**
     * 根据合约地址获取缓存
     * @param contractAddress
     * @return
     */
    public TokenPrices getTokenCacheByContractAddress(String contractAddress){
        String key=TOKEN_PREFIX+contractAddress.toLowerCase();
        return redisCache.getCacheObject(key);
    }

    /**
     * 获取链参数缓存
     * @param key
     * @return
     */
    @Override
    public BigDecimal getChainParamCache(String key){
        if (StringUtils.isBlank(key)) {
            return null;
        }
        return redisCache.getCacheObject(CHAIN_PARAM_PREFIX + key);
    }

    /**
     * 设置链参数缓存
     * @param key
     * @param value
     * @param timeoutSeconds
     */
    @Override
    public void setChainParamCache(String key, BigDecimal value, long timeoutSeconds){
        if (StringUtils.isBlank(key) || value == null) {
            return;
        }
        int ttlSeconds = (int) Math.min(timeoutSeconds, Integer.MAX_VALUE);
        redisCache.setCacheObject(CHAIN_PARAM_PREFIX + key, value, ttlSeconds, TimeUnit.SECONDS);
    }

    /**
     * 设置缓存
     * @param token
     */
    private void setCache(TokenPrices token){
        String key="";
        if (token.getTokenContract() != null && !token.getTokenContract().isEmpty()) {
            key=TOKEN_PREFIX+token.getTokenContract().toLowerCase();
            redisCache.setCacheObject(key,token);
        }
        key=TOKEN_PREFIX+(token.getChainType()+":"+token.getTokenSymbol()).toUpperCase();
        redisCache.setCacheObject(key,token);
    }

    /**
     * 删除缓存
     */
    private void delCache(){
        String key=TOKEN_PREFIX+"*";
        redisCache.deleteObject(redisCache.getCacheSet(key));
    }
    /**
     *
     * @param chainType
     * @return
     */
    public List<TokenPrices> getEnabledTokens(String chainType) {
        TokenPrices w=new TokenPrices();
        PageHelper.orderBy("is_main_coin asc");
        PageHelper.offsetPage(0,20);
        if (StringUtils.isNotBlank(chainType)){
            w.setChainType(chainType.toUpperCase());
        }
        w.setEnabled(1);
       return this.selectTokenPricesList(w);
    }

    /**
     * 新增币种配置
     * 
     * @param tokenPrices 币种配置
     * @return 结果
     */
    @Override
    public int insertTokenPrices(TokenPrices tokenPrices)
    {
        tokenPrices.setCreateTime(DateUtils.getNowDate());
        int flag= tokenPricesMapper.insertTokenPrices(tokenPrices);
        if (flag>0){
            setCache(tokenPrices);
        }
        return flag;
    }

    /**
     * 修改币种配置
     * 
     * @param tokenPrices 币种配置
     * @return 结果
     */
    @Override
    public int updateTokenPrices(TokenPrices tokenPrices)
    {
        tokenPrices.setUpdateTime(DateUtils.getNowDate());

        int flag= tokenPricesMapper.updateTokenPrices(tokenPrices);
        if (flag>0){
            setCache(tokenPrices);
        }
        return flag;
    }
    public  boolean updatePrice(String tokenSymbol, java.math.BigDecimal price){
        TokenPrices tokenPrices = BeanUtils.instantiateClass(TokenPrices.class);
        tokenPrices.setTokenSymbol(tokenSymbol);
        tokenPrices.setPriceInUsdt(price);
        tokenPrices.setLastUpdate(DateUtils.getNowDate());
        int flag= tokenPricesMapper.updatePriceForSymbol(tokenPrices);
        if (flag>0){
            initTokenConfigCache(null);
        }
        return flag>0;
    }

    /**
     * 批量删除币种配置
     * 
     * @param ids 需要删除的币种配置主键
     * @return 结果
     */
    @Override
    public int deleteTokenPricesByIds(String ids)
    {
        delCache();
        return tokenPricesMapper.deleteTokenPricesByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除币种配置信息
     * 
     * @param id 币种配置主键
     * @return 结果
     */
    @Override
    public int deleteTokenPricesById(Long id)
    {
        delCache();
        return tokenPricesMapper.deleteTokenPricesById(id);
    }
}
