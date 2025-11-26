package com.ruoyi.blockchain.service;

import java.util.List;

import com.ruoyi.blockchain.domain.ApiBcTransaction;
import com.ruoyi.blockchain.domain.BcTransactions;
import com.ruoyi.blockchain.domain.TronTransaction;

/**
 * 地址充值记录Service接口
 * 
 * @author dc
 * @date 2025-10-30
 */
public interface IBcTransactionsService 
{
    /**
     * 查询地址充值记录
     * 
     * @param id 地址充值记录主键
     * @return 地址充值记录
     */
    public BcTransactions selectBcTransactionsById(Long id);

    /**
     * 查询地址充值记录列表
     * 
     * @param bcTransactions 地址充值记录
     * @return 地址充值记录集合
     */
    public List<BcTransactions> selectBcTransactionsList(BcTransactions bcTransactions);

    /**
     * 查询地址充值记录
     *
     * @param txHash 交易hash
     * @return 地址充值记录
     */
     BcTransactions selectBcTransactionsByHash(String txHash);
    /**
     * 新增地址充值记录
     * 
     * @param bcTransactions 地址充值记录
     * @return 结果
     */
    public int insertBcTransactions(BcTransactions bcTransactions);


    /**
     * 处理监控来的地址充值记录
     *
     * @param transactions 地址充值记录
     * @return 结果
     */
    public boolean saveBcTransactions(String chainType, Long addressId, ApiBcTransaction transactions);

    /**
     * 修改地址充值记录
     * 
     * @param bcTransactions 地址充值记录
     * @return 结果
     */
    public int updateBcTransactions(BcTransactions bcTransactions);

    /**
     * 批量删除地址充值记录
     * 
     * @param ids 需要删除的地址充值记录主键集合
     * @return 结果
     */
    public int deleteBcTransactionsByIds(String ids);

    /**
     * 删除地址充值记录信息
     * 
     * @param id 地址充值记录主键
     * @return 结果
     */
    public int deleteBcTransactionsById(Long id);
}
