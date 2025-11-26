package com.ruoyi.blockchain.mapper;

import java.util.List;
import com.ruoyi.blockchain.domain.BcTransactions;

/**
 * 地址充值记录Mapper接口
 * 
 * @author dc
 * @date 2025-10-30
 */
public interface BcTransactionsMapper 
{
    /**
     * 查询地址充值记录
     * 
     * @param id 地址充值记录主键
     * @return 地址充值记录
     */
    public BcTransactions selectBcTransactionsById(Long id);

    /**
     * 查询地址充值记录
     *
     * @param txHash 交易hash
     * @return 地址充值记录
     */
     BcTransactions selectBcTransactionByHash(String txHash);

    /**
     * 查询地址充值记录列表
     * 
     * @param bcTransactions 地址充值记录
     * @return 地址充值记录集合
     */
    public List<BcTransactions> selectBcTransactionsList(BcTransactions bcTransactions);

    /**
     * 新增地址充值记录
     * 
     * @param bcTransactions 地址充值记录
     * @return 结果
     */
    public int insertBcTransactions(BcTransactions bcTransactions);

    /**
     * 修改地址充值记录
     * 
     * @param bcTransactions 地址充值记录
     * @return 结果
     */
    public int updateBcTransactions(BcTransactions bcTransactions);

    /**
     * 删除地址充值记录
     * 
     * @param id 地址充值记录主键
     * @return 结果
     */
    public int deleteBcTransactionsById(Long id);

    /**
     * 批量删除地址充值记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBcTransactionsByIds(String[] ids);
}
