package com.ruoyi.blockchain.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.pagehelper.PageHelper;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.blockchain.mapper.BcApiKeysMapper;
import com.ruoyi.blockchain.domain.BcApiKeys;
import com.ruoyi.blockchain.service.IBcApiKeysService;
import com.ruoyi.common.core.text.Convert;

/**
 * 区块API配置Service业务层处理
 * 
 * @author dc
 * @date 2025-10-29
 */
@Service
public class BcApiKeysServiceImpl implements IBcApiKeysService 
{
    @Autowired
    private BcApiKeysMapper bcApiKeysMapper;
    @Autowired
    private RedisCache redisCache;

    private static final String CACHE_KEY_PREFIX = "bc:apikeys:";
    private static final String CACHE_LIST_PREFIX = "bc:apikeys:list:";
    //private static final String USAGE_KEY_PREFIX = "bc:apikey:usage:";
    private static final String LAST_RESET_KEY = "bc:apikeys:last_reset";
    /**
     * 查询区块API配置
     * 
     * @param id 区块API配置主键
     * @return 区块API配置
     */
    @Override
    public BcApiKeys selectBcApiKeysById(Long id)
    {
        return bcApiKeysMapper.selectBcApiKeysById(id);
    }



    /**
     * 查询区块API配置列表
     * 
     * @param bcApiKeys 区块API配置
     * @return 区块API配置
     */
    @Override
    public List<BcApiKeys> selectBcApiKeysList(BcApiKeys bcApiKeys)
    {
        return bcApiKeysMapper.selectBcApiKeysList(bcApiKeys);
    }

    /**
     * 新增区块API配置
     * 
     * @param bcApiKeys 区块API配置
     * @return 结果
     */
    @Override
    public int insertBcApiKeys(BcApiKeys bcApiKeys)
    {
        bcApiKeys.setCreateTime(DateUtils.getNowDate());
        int flag= bcApiKeysMapper.insertBcApiKeys(bcApiKeys);
        if(flag>0){
            setCache(bcApiKeys);
            invalidateListCache(bcApiKeys.getChainType());
        }
        return flag;
    }

    /**
     * 修改区块API配置
     * 
     * @param bcApiKeys 区块API配置
     * @return 结果
     */
    @Override
    public int updateBcApiKeys(BcApiKeys bcApiKeys)
    {

        bcApiKeys.setUpdateTime(DateUtils.getNowDate());
        int flag= bcApiKeysMapper.updateBcApiKeys(bcApiKeys);
        if(flag>0){
            setCache(bcApiKeys);
            invalidateListCache(bcApiKeys.getChainType());
        }
        return flag;
    }

    @Override
    public BcApiKeys getAvailableKey(String chainType) {
        // 1. 检查是否需要重置计数（跨天）
        checkAndResetDailyCount();

        // 优先从缓存获取
        List<BcApiKeys> cachedKeys= getCacheList(chainType);
        if (cachedKeys.isEmpty()) {
            // 缓存没有，从DB加载
            BcApiKeys query = new BcApiKeys();
            query.setChainType(chainType);
            query.setStatus("0");
            PageHelper.orderBy("priority asc");
            cachedKeys = bcApiKeysMapper.selectBcApiKeysList(query);
            setCacheList(cachedKeys);
        }
        // 3. 选择未达限制的 Key
        for (BcApiKeys key : cachedKeys) {
            if ("2".equals(key.getStatus())){
                continue;
            }
            if (key.getUsedCount() < key.getDailyLimit()) {
                return key;
            }
        }
        return null;
    }

    @Override
    public void recordUsage(Long keyId) {

        //String usageKey = USAGE_KEY_PREFIX + keyId;

        // Redis 原子递增（不会丢数据）
       // Long newCount=redisCache.increment(usageKey);
        BcApiKeys apiKeyDb = this.selectBcApiKeysById(keyId);
        if (apiKeyDb == null) {
            return;
        }
       // String cacheKey=getCacheKey(key.getChainType(),keyId);
        BcApiKeys apiKeyCache=getCacheOne(apiKeyDb.getChainType(),keyId);
        if (apiKeyCache == null) {
            apiKeyCache = apiKeyDb;
            setCache(apiKeyCache);
        }
        //使用次数+1
        Long cacheUsed=(apiKeyCache.getUsedCount() == null ? 0L : apiKeyCache.getUsedCount()) + 1;

        Long dbUsed=apiKeyDb.getUsedCount() == null ? 0L : apiKeyDb.getUsedCount();
        //保存到缓存
        apiKeyCache.setUsedCount(cacheUsed);
        setCache(apiKeyCache);
        //20个更新一次数据库
        if ((cacheUsed-dbUsed)<20L){
            return;
        }
        // 增加使用次数
        BcApiKeys updateKey = new BcApiKeys();
        updateKey.setUsedCount(cacheUsed);
        updateKey.setLastUsedTime(DateUtils.getNowDate());
        updateKey.setId(keyId);
        // 检查是否达到限制
        if (cacheUsed >= apiKeyDb.getDailyLimit()) {
            updateKey.setStatus("2"); // 已达上限
            apiKeyCache.setStatus("2");
        }
        setCache(apiKeyCache);
        bcApiKeysMapper.updateBcApiKeys(updateKey);
    }

    @Override
    public void resetDailyCount() {
        // 重置所有 Key 的计数
       bcApiKeysMapper.updateResetDailyCount();

       //重新处理缓存
        BcApiKeys query = new BcApiKeys();
        query.setStatus("0");
        PageHelper.orderBy("priority asc");
        List<BcApiKeys> cachedKeys = bcApiKeysMapper.selectBcApiKeysList(query);
        setCacheList(cachedKeys);
       // deleteCache();
        // 更新重置时间
        redisCache.setCacheObject(LAST_RESET_KEY,LocalDate.now().toString());
    }
    private void deleteCache(){
        // 清理缓存
        String key=CACHE_KEY_PREFIX + "*";
        redisCache.deleteObject(redisCache.keys(key));
        redisCache.deleteObject(redisCache.keys(CACHE_LIST_PREFIX + "*"));
    }

    /**
     * 单条保存
     * @param apiKey
     */
    private void  setCache(BcApiKeys apiKey){
        // 优先从缓存获取
        String key =getCacheKey(apiKey.getChainType(),apiKey.getId());
        redisCache.setCacheObject(key,apiKey);
    }

    /**
     * 批量保存
     * @param apiKeysList
     */
    private void  setCacheList(List<BcApiKeys> apiKeysList){
        if (apiKeysList == null || apiKeysList.isEmpty()) {
            return;
        }
        redisCache.setCacheList(getListCacheKey(apiKeysList.get(0).getChainType()), apiKeysList);
        // 批量设置缓存
        for (BcApiKeys apiKey:apiKeysList) {
           setCache(apiKey);
        }
    }

    /**
     * 返回单条的id
     * @param chainType
     * @param id
     * @return
     */
    private BcApiKeys getCacheOne(String chainType,Long id){
        String key =getCacheKey(chainType,id);
        return redisCache.getCacheObject(key);
    }

    /**
     * 没有id就返回整个list
     * @param chainType
     * @return
     */
    private List<BcApiKeys> getCacheList(String chainType){
        List<BcApiKeys> list = new ArrayList<>();
        List<BcApiKeys> cacheList = redisCache.getCacheList(getListCacheKey(chainType));
        if (cacheList != null && !cacheList.isEmpty()) {
            return cacheList;
        }
        //没有传入id，返回整个list
        String key =getCacheKey(chainType,0L);
        Collection<String> keys= redisCache.keys(key);
        if (keys == null || keys.isEmpty()) {
            return list;
        }
        for (String k : keys) {
            BcApiKeys v=redisCache.getCacheObject(k);
            if (v != null) {
                list.add(v);
            }
        }

        return list;
    }

    private void invalidateListCache(String chainType) {
        redisCache.deleteObject(getListCacheKey(chainType));
    }

    private String getListCacheKey(String chainType) {
        return (CACHE_LIST_PREFIX + chainType).toLowerCase();
    }

    /**
     * 获取key，都转小写
     * @param chainType
     * @param id
     * @return
     */
    private String getCacheKey(String chainType,Long id){
        String cacheKey = CACHE_KEY_PREFIX + chainType;
        if (id!=null && id>0L){
            cacheKey= cacheKey+":"+id;
        }else{
            cacheKey= cacheKey+":*";
        }
        return cacheKey.toLowerCase();
    }


    private void checkAndResetDailyCount() {
        // 从 Redis 获取上次重置时间
        String lastResetTimeStr =redisCache.getCacheObject(LAST_RESET_KEY);
        LocalDate today = LocalDate.now();

        if (lastResetTimeStr != null) {
            LocalDate lastResetDate = LocalDate.parse(lastResetTimeStr);
            if (!today.isAfter(lastResetDate)) {
                // 当天已经重置过，直接返回
                return;
            }
        } else {
            redisCache.setCacheObject(LAST_RESET_KEY, LocalDate.now().toString());
            return;
        }
        // 执行重置操作
        resetDailyCount();
    }
    /**
     * 批量删除区块API配置
     * 
     * @param ids 需要删除的区块API配置主键
     * @return 结果
     */
    @Override
    public int deleteBcApiKeysByIds(String ids)
    {
        deleteCache();
        return bcApiKeysMapper.deleteBcApiKeysByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除区块API配置信息
     * 
     * @param id 区块API配置主键
     * @return 结果
     */
    @Override
    public int deleteBcApiKeysById(Long id)
    {
        deleteCache();
        return bcApiKeysMapper.deleteBcApiKeysById(id);
    }
}
