package com.ruoyi.blockchain.mapper;

import java.util.List;
import com.ruoyi.blockchain.domain.BcApiKeys;

/**
 * 区块API配置Mapper接口
 * 
 * @author dc
 * @date 2025-10-29
 */
public interface BcApiKeysMapper 
{
    /**
     * 查询区块API配置
     * 
     * @param id 区块API配置主键
     * @return 区块API配置
     */
    public BcApiKeys selectBcApiKeysById(Long id);

    /**
     * 查询区块API配置列表
     * 
     * @param bcApiKeys 区块API配置
     * @return 区块API配置集合
     */
    public List<BcApiKeys> selectBcApiKeysList(BcApiKeys bcApiKeys);

    /**
     * 查询区块API配置列表
     *
     * @param
     * @return 获取已经使用过的配置集合
     */
    List<BcApiKeys> selectDailyCount();


    /**
     * 新增区块API配置
     * 
     * @param bcApiKeys 区块API配置
     * @return 结果
     */
    public int insertBcApiKeys(BcApiKeys bcApiKeys);

    /**
     * 修改区块API配置
     * 
     * @param bcApiKeys 区块API配置
     * @return 结果
     */
    public int updateBcApiKeys(BcApiKeys bcApiKeys);

    /**
     * 修改区块API配置
     *
     * @return 结果
     */
    public int updateResetDailyCount();

    /**
     * 删除区块API配置
     * 
     * @param id 区块API配置主键
     * @return 结果
     */
    public int deleteBcApiKeysById(Long id);

    /**
     * 批量删除区块API配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBcApiKeysByIds(String[] ids);
}
