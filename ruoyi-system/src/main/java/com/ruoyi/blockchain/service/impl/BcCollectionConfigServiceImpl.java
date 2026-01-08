package com.ruoyi.blockchain.service.impl;

import java.util.List;

import com.ruoyi.blockchain.domain.TokenPrices;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.ruoyi.blockchain.mapper.BcCollectionConfigMapper;
import com.ruoyi.blockchain.domain.BcCollectionConfig;
import com.ruoyi.blockchain.service.IBcCollectionConfigService;
import com.ruoyi.common.core.text.Convert;

/**
 * 归集配置Service业务层处理
 * 
 * @author dc
 * @date 2025-11-09
 */
@Service
public class BcCollectionConfigServiceImpl implements IBcCollectionConfigService 
{
    @Autowired
    private BcCollectionConfigMapper bcCollectionConfigMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /** 币种配置 */
    public static final String COLLECTION_PREFIX = "bc:collection:config:";
    /**
     * 查询归集配置
     * 
     * @param id 归集配置主键
     * @return 归集配置
     */
    @Override
    public BcCollectionConfig selectBcCollectionConfigById(Long id)
    {
        return bcCollectionConfigMapper.selectBcCollectionConfigById(id);
    }

    /**
     * 获取归集配置缓存
     * @param platformId
     * @param chainType
     * @return
     */
    public BcCollectionConfig getCollectionConfigCache(Long platformId,String chainType){
        String key=getKey(platformId,chainType);
        Object obj=redisTemplate.opsForValue().get(key);
        if (obj==null){
           List<BcCollectionConfig> configList= bcCollectionConfigMapper.getCollectionConfigByplatformId(platformId);
            for (BcCollectionConfig config:configList) {
                if (config.getChainType().equalsIgnoreCase(chainType)){
                    obj=config;
                }
                setCache(platformId,config);
            }

        }
        return (BcCollectionConfig)obj;
    }

    /**
     * 设置缓存
     * @param platformId
     * @param config
     */
    private void setCache(Long platformId,BcCollectionConfig config){
        String key=getKey(platformId,config.getChainType());
        redisTemplate.opsForValue().set(key, config);
    }

    /**
     * 拼接key
     * @param platformId
     * @param chainType
     * @return
     */
    private String getKey(Long platformId,String chainType){
        return COLLECTION_PREFIX+platformId+":"+chainType.toLowerCase();
    }

    /**
     * 查询归集配置
     *
     * @param platformId 平台id
     * @return 归集配置
     */
    @Override
    public List<BcCollectionConfig>  getCollectionConfigByplatformId(Long platformId)
    {
        return bcCollectionConfigMapper.getCollectionConfigByplatformId(platformId);
    }

    /**
     * 查询归集配置列表
     * 
     * @param bcCollectionConfig 归集配置
     * @return 归集配置
     */
    @Override
    public List<BcCollectionConfig> selectBcCollectionConfigList(BcCollectionConfig bcCollectionConfig)
    {
        return bcCollectionConfigMapper.selectBcCollectionConfigList(bcCollectionConfig);
    }

    /**
     * 新增归集配置
     * 
     * @param bcCollectionConfig 归集配置
     * @return 结果
     */
    @Override
    public int insertBcCollectionConfig(BcCollectionConfig bcCollectionConfig)
    {
        try {
            bcCollectionConfig.setCreateTime(DateUtils.getNowDate());
            int flag= bcCollectionConfigMapper.insertBcCollectionConfig(bcCollectionConfig);

            if (flag>0){
                setCache(bcCollectionConfig.getPlatformId(),bcCollectionConfig);
            }
            return flag;
        }catch (Exception e){
            throw  new ServiceException("不可重复添加");
        }


    }

    /**
     * 修改归集配置
     * 
     * @param bcCollectionConfig 归集配置
     * @return 结果
     */
    @Override
    public int updateBcCollectionConfig(BcCollectionConfig bcCollectionConfig)
    {
        try {
            bcCollectionConfig.setUpdateTime(DateUtils.getNowDate());
            int flag = bcCollectionConfigMapper.updateBcCollectionConfig(bcCollectionConfig);
            if (flag > 0) {
                setCache(bcCollectionConfig.getPlatformId(), bcCollectionConfig);
            }
            return flag;
        }catch (Exception e){
            throw  new ServiceException("同一平台只能添加一个链");
        }
    }

    /**
     * 批量删除归集配置
     * 
     * @param ids 需要删除的归集配置主键
     * @return 结果
     */
    @Override
    public int deleteBcCollectionConfigByIds(String ids)
    {
        // 清理缓存
        redisTemplate.delete(redisTemplate.keys(COLLECTION_PREFIX + "*"));

        return bcCollectionConfigMapper.deleteBcCollectionConfigByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除归集配置信息
     * 
     * @param id 归集配置主键
     * @return 结果
     */
    @Override
    public int deleteBcCollectionConfigById(Long id)
    {
        // 清理缓存
        redisTemplate.delete(redisTemplate.keys(COLLECTION_PREFIX + "*"));

        return bcCollectionConfigMapper.deleteBcCollectionConfigById(id);
    }
}
