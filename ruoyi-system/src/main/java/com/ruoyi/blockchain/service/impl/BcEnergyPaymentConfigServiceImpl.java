package com.ruoyi.blockchain.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.ruoyi.blockchain.mapper.BcEnergyPaymentConfigMapper;
import com.ruoyi.blockchain.domain.BcEnergyPaymentConfig;
import com.ruoyi.blockchain.service.IBcEnergyPaymentConfigService;
import com.ruoyi.common.core.text.Convert;

/**
 * 能量API配置Service业务层处理
 * 
 * @author dc
 * @date 2026-01-08
 */
@Service
public class BcEnergyPaymentConfigServiceImpl implements IBcEnergyPaymentConfigService 
{
    @Autowired
    private BcEnergyPaymentConfigMapper bcEnergyPaymentConfigMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /** 能量支付配置缓存前缀 */
    public static final String ENERGY_PAYMENT_PREFIX = "bc:energy:payment:config:";

    /**
     * 查询能量API配置
     * 
     * @param id 能量API配置主键
     * @return 能量API配置
     */
    @Override
    public BcEnergyPaymentConfig selectBcEnergyPaymentConfigById(Long id)
    {
        return bcEnergyPaymentConfigMapper.selectBcEnergyPaymentConfigById(id);
    }

    /**
     * 获取能量API配置缓存
     *
     * @param platformId 平台ID
     * @param chainType 链类型
     * @return 能量API配置
     */
    @Override
    public BcEnergyPaymentConfig getEnergyPaymentConfigCache(Long platformId, String chainType)
    {
        String key = getKey(platformId, chainType);
        Object obj = redisTemplate.opsForValue().get(key);
        if (obj == null) {
            BcEnergyPaymentConfig query = new BcEnergyPaymentConfig();
            query.setPlatformId(platformId);
            List<BcEnergyPaymentConfig> configList =
                bcEnergyPaymentConfigMapper.selectBcEnergyPaymentConfigList(query);
            for (BcEnergyPaymentConfig config : configList) {
                if (config.getChainType().equalsIgnoreCase(chainType)) {
                    obj = config;
                }
                setCache(platformId, config);
            }
        }
        return (BcEnergyPaymentConfig) obj;
    }

    /**
     * 设置缓存
     *
     * @param platformId 平台ID
     * @param config 配置
     */
    private void setCache(Long platformId, BcEnergyPaymentConfig config)
    {
        String key = getKey(platformId, config.getChainType());
        redisTemplate.opsForValue().set(key, config);
    }

    /**
     * 拼接key
     *
     * @param platformId 平台ID
     * @param chainType 链类型
     * @return key
     */
    private String getKey(Long platformId, String chainType)
    {
        return ENERGY_PAYMENT_PREFIX + platformId + ":" + chainType.toLowerCase();
    }

    /**
     * 查询能量API配置列表
     * 
     * @param bcEnergyPaymentConfig 能量API配置
     * @return 能量API配置
     */
    @Override
    public List<BcEnergyPaymentConfig> selectBcEnergyPaymentConfigList(BcEnergyPaymentConfig bcEnergyPaymentConfig)
    {
        return bcEnergyPaymentConfigMapper.selectBcEnergyPaymentConfigList(bcEnergyPaymentConfig);
    }

    /**
     * 新增能量API配置
     * 
     * @param bcEnergyPaymentConfig 能量API配置
     * @return 结果
     */
    @Override
    public int insertBcEnergyPaymentConfig(BcEnergyPaymentConfig bcEnergyPaymentConfig)
    {
        bcEnergyPaymentConfig.setCreateTime(DateUtils.getNowDate());
        int flag = bcEnergyPaymentConfigMapper.insertBcEnergyPaymentConfig(bcEnergyPaymentConfig);
        if (flag > 0) {
            setCache(bcEnergyPaymentConfig.getPlatformId(), bcEnergyPaymentConfig);
        }
        return flag;
    }

    /**
     * 修改能量API配置
     * 
     * @param bcEnergyPaymentConfig 能量API配置
     * @return 结果
     */
    @Override
    public int updateBcEnergyPaymentConfig(BcEnergyPaymentConfig bcEnergyPaymentConfig)
    {
        bcEnergyPaymentConfig.setUpdateTime(DateUtils.getNowDate());
        int flag = bcEnergyPaymentConfigMapper.updateBcEnergyPaymentConfig(bcEnergyPaymentConfig);
        if (flag > 0) {
            setCache(bcEnergyPaymentConfig.getPlatformId(), bcEnergyPaymentConfig);
        }
        return flag;
    }

    /**
     * 批量删除能量API配置
     * 
     * @param ids 需要删除的能量API配置主键
     * @return 结果
     */
    @Override
    public int deleteBcEnergyPaymentConfigByIds(String ids)
    {
        redisTemplate.delete(redisTemplate.keys(ENERGY_PAYMENT_PREFIX + "*"));
        return bcEnergyPaymentConfigMapper.deleteBcEnergyPaymentConfigByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除能量API配置信息
     * 
     * @param id 能量API配置主键
     * @return 结果
     */
    @Override
    public int deleteBcEnergyPaymentConfigById(Long id)
    {
        redisTemplate.delete(redisTemplate.keys(ENERGY_PAYMENT_PREFIX + "*"));
        return bcEnergyPaymentConfigMapper.deleteBcEnergyPaymentConfigById(id);
    }
}
