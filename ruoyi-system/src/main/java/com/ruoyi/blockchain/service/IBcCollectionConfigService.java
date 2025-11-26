package com.ruoyi.blockchain.service;

import java.util.List;
import com.ruoyi.blockchain.domain.BcCollectionConfig;

/**
 * 归集配置Service接口
 * 
 * @author dc
 * @date 2025-11-09
 */
public interface IBcCollectionConfigService 
{
    /**
     * 查询归集配置
     * 
     * @param id 归集配置主键
     * @return 归集配置
     */
    public BcCollectionConfig selectBcCollectionConfigById(Long id);

    /**
     * 获取归集配置缓存
     * @param platformId
     * @param chainType
     * @return
     */
     BcCollectionConfig getCollectionConfigCache(Long platformId,String chainType);
    /**
     * 查询归集配置
     *
     * @param platformId 平台id
     * @return 归集配置
     */
    List<BcCollectionConfig> getCollectionConfigByplatformId(Long platformId);

    /**
     * 查询归集配置列表
     * 
     * @param bcCollectionConfig 归集配置
     * @return 归集配置集合
     */
    public List<BcCollectionConfig> selectBcCollectionConfigList(BcCollectionConfig bcCollectionConfig);

    /**
     * 新增归集配置
     * 
     * @param bcCollectionConfig 归集配置
     * @return 结果
     */
    public int insertBcCollectionConfig(BcCollectionConfig bcCollectionConfig);

    /**
     * 修改归集配置
     * 
     * @param bcCollectionConfig 归集配置
     * @return 结果
     */
    public int updateBcCollectionConfig(BcCollectionConfig bcCollectionConfig);

    /**
     * 批量删除归集配置
     * 
     * @param ids 需要删除的归集配置主键集合
     * @return 结果
     */
    public int deleteBcCollectionConfigByIds(String ids);

    /**
     * 删除归集配置信息
     * 
     * @param id 归集配置主键
     * @return 结果
     */
    public int deleteBcCollectionConfigById(Long id);
}
