package com.ruoyi.blockchain.service;

import java.util.List;
import com.ruoyi.blockchain.domain.BcWithdrawRecord;

/**
 * 区块链提现记录Service接口
 * 
 * @author dc
 * @date 2025-11-12
 */
public interface IBcWithdrawRecordService 
{
    /**
     * 查询区块链提现记录
     * 
     * @param id 区块链提现记录主键
     * @return 区块链提现记录
     */
    public BcWithdrawRecord selectBcWithdrawRecordById(Long id);

    /**
     * 查询区块链提现记录列表
     * 
     * @param bcWithdrawRecord 区块链提现记录
     * @return 区块链提现记录集合
     */
    public List<BcWithdrawRecord> selectBcWithdrawRecordList(BcWithdrawRecord bcWithdrawRecord);

    /**
     * 新增区块链提现记录
     * 
     * @param bcWithdrawRecord 区块链提现记录
     * @return 结果
     */
    public int insertBcWithdrawRecord(BcWithdrawRecord bcWithdrawRecord);

    /**
     * 修改区块链提现记录
     * 
     * @param bcWithdrawRecord 区块链提现记录
     * @return 结果
     */
    public int updateBcWithdrawRecord(BcWithdrawRecord bcWithdrawRecord);

    /**
     * 批量删除区块链提现记录
     * 
     * @param ids 需要删除的区块链提现记录主键集合
     * @return 结果
     */
    public int deleteBcWithdrawRecordByIds(String ids);

    /**
     * 删除区块链提现记录信息
     * 
     * @param id 区块链提现记录主键
     * @return 结果
     */
    public int deleteBcWithdrawRecordById(Long id);
}
