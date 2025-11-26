package com.ruoyi.blockchain.service;

import java.util.List;
import com.ruoyi.blockchain.domain.BcApiKeys;

/**
 * 区块API配置Service接口
 * 
 * @author dc
 * @date 2025-10-29
 */
public interface IBcApiKeysService 
{
    /**
     * 查询区块API配置
     * 
     * @param id 区块API配置主键
     * @return 区块API配置
     */
    public BcApiKeys selectBcApiKeysById(Long id);

    /**
     * 获取可用的 API Key（轮询策略）
     */
    BcApiKeys getAvailableKey(String chainType);

    /**
     * 记录 API Key 使用
     */
    void recordUsage(Long keyId);

    /**
     * 重置每日计数
     */
    void resetDailyCount();

    /**
     * 查询区块API配置列表
     * 
     * @param bcApiKeys 区块API配置
     * @return 区块API配置集合
     */
    public List<BcApiKeys> selectBcApiKeysList(BcApiKeys bcApiKeys);

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
     * 批量删除区块API配置
     * 
     * @param ids 需要删除的区块API配置主键集合
     * @return 结果
     */
    public int deleteBcApiKeysByIds(String ids);

    /**
     * 删除区块API配置信息
     * 
     * @param id 区块API配置主键
     * @return 结果
     */
    public int deleteBcApiKeysById(Long id);
}
