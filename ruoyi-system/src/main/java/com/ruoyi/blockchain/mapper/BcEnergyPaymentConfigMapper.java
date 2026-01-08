package com.ruoyi.blockchain.mapper;

import java.util.List;
import com.ruoyi.blockchain.domain.BcEnergyPaymentConfig;

/**
 * 能量API配置Mapper接口
 * 
 * @author dc
 * @date 2026-01-08
 */
public interface BcEnergyPaymentConfigMapper 
{
    /**
     * 查询能量API配置
     * 
     * @param id 能量API配置主键
     * @return 能量API配置
     */
    public BcEnergyPaymentConfig selectBcEnergyPaymentConfigById(Long id);

    /**
     * 查询能量API配置列表
     * 
     * @param bcEnergyPaymentConfig 能量API配置
     * @return 能量API配置集合
     */
    public List<BcEnergyPaymentConfig> selectBcEnergyPaymentConfigList(BcEnergyPaymentConfig bcEnergyPaymentConfig);

    /**
     * 新增能量API配置
     * 
     * @param bcEnergyPaymentConfig 能量API配置
     * @return 结果
     */
    public int insertBcEnergyPaymentConfig(BcEnergyPaymentConfig bcEnergyPaymentConfig);

    /**
     * 修改能量API配置
     * 
     * @param bcEnergyPaymentConfig 能量API配置
     * @return 结果
     */
    public int updateBcEnergyPaymentConfig(BcEnergyPaymentConfig bcEnergyPaymentConfig);

    /**
     * 删除能量API配置
     * 
     * @param id 能量API配置主键
     * @return 结果
     */
    public int deleteBcEnergyPaymentConfigById(Long id);

    /**
     * 批量删除能量API配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBcEnergyPaymentConfigByIds(String[] ids);
}
