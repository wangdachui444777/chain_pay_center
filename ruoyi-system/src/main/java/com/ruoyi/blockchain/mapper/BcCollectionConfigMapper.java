package com.ruoyi.blockchain.mapper;

import java.util.List;
import com.ruoyi.blockchain.domain.BcCollectionConfig;

/**
 * 归集配置Mapper接口
 * 
 * @author dc
 * @date 2025-11-09
 */
public interface BcCollectionConfigMapper 
{
    /**
     * 查询归集配置
     * 
     * @param id 归集配置主键
     * @return 归集配置
     */
    public BcCollectionConfig selectBcCollectionConfigById(Long id);

    /**
     * 查询归集配置
     *
     * @param platformId 平台id
     * @return 归集配置
     */
    List<BcCollectionConfig>  getCollectionConfigByplatformId(Long platformId);
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
     * 删除归集配置
     * 
     * @param id 归集配置主键
     * @return 结果
     */
    public int deleteBcCollectionConfigById(Long id);

    /**
     * 批量删除归集配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBcCollectionConfigByIds(String[] ids);
}
