package com.ruoyi.blockchain.mapper;

import java.util.List;
import com.ruoyi.blockchain.domain.Platforms;

/**
 * 下游平台Mapper接口
 * 
 * @author dc
 * @date 2025-10-27
 */
public interface PlatformsMapper 
{
    /**
     * 查询下游平台
     * 
     * @param id 下游平台主键
     * @return 下游平台
     */
    public Platforms selectPlatformsById(Long id);

    /**
     * 查询下游平台列表
     * 
     * @param platforms 下游平台
     * @return 下游平台集合
     */
    public List<Platforms> selectPlatformsList(Platforms platforms);

    /**
     * 获取所有的平台列表，用于下拉查询
     * @return
     */
    List<Platforms> getPlatformsAll();

    /**
     * 新增下游平台
     * 
     * @param platforms 下游平台
     * @return 结果
     */
    public int insertPlatforms(Platforms platforms);

    /**
     * 修改下游平台
     * 
     * @param platforms 下游平台
     * @return 结果
     */
    public int updatePlatforms(Platforms platforms);

    /**
     * 删除下游平台
     * 
     * @param id 下游平台主键
     * @return 结果
     */
    public int deletePlatformsById(Long id);

    /**
     * 批量删除下游平台
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePlatformsByIds(String[] ids);
}
