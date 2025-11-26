package com.ruoyi.blockchain.service.impl;

import java.util.List;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.blockchain.mapper.BcWithdrawRecordMapper;
import com.ruoyi.blockchain.domain.BcWithdrawRecord;
import com.ruoyi.blockchain.service.IBcWithdrawRecordService;
import com.ruoyi.common.core.text.Convert;

/**
 * 区块链提现记录Service业务层处理
 * 
 * @author dc
 * @date 2025-11-12
 */
@Service
public class BcWithdrawRecordServiceImpl implements IBcWithdrawRecordService 
{
    @Autowired
    private BcWithdrawRecordMapper bcWithdrawRecordMapper;

    /**
     * 查询区块链提现记录
     * 
     * @param id 区块链提现记录主键
     * @return 区块链提现记录
     */
    @Override
    public BcWithdrawRecord selectBcWithdrawRecordById(Long id)
    {
        return bcWithdrawRecordMapper.selectBcWithdrawRecordById(id);
    }

    /**
     * 查询区块链提现记录列表
     * 
     * @param bcWithdrawRecord 区块链提现记录
     * @return 区块链提现记录
     */
    @Override
    public List<BcWithdrawRecord> selectBcWithdrawRecordList(BcWithdrawRecord bcWithdrawRecord)
    {
        return bcWithdrawRecordMapper.selectBcWithdrawRecordList(bcWithdrawRecord);
    }

    /**
     * 新增区块链提现记录
     * 
     * @param bcWithdrawRecord 区块链提现记录
     * @return 结果
     */
    @Override
    public int insertBcWithdrawRecord(BcWithdrawRecord bcWithdrawRecord)
    {
        try {
            bcWithdrawRecord.setCreateTime(DateUtils.getNowDate());
            bcWithdrawRecord.setTxStatus("0"); //待提交
            return bcWithdrawRecordMapper.insertBcWithdrawRecord(bcWithdrawRecord);
        }catch (Exception e){
            throw new ServiceException("添加数据错误");
        }

    }

    /**
     * 修改区块链提现记录
     * 
     * @param bcWithdrawRecord 区块链提现记录
     * @return 结果
     */
    @Override
    public int updateBcWithdrawRecord(BcWithdrawRecord bcWithdrawRecord)
    {
        bcWithdrawRecord.setUpdateTime(DateUtils.getNowDate());
        return bcWithdrawRecordMapper.updateBcWithdrawRecord(bcWithdrawRecord);
    }

    /**
     * 批量删除区块链提现记录
     * 
     * @param ids 需要删除的区块链提现记录主键
     * @return 结果
     */
    @Override
    public int deleteBcWithdrawRecordByIds(String ids)
    {
        return bcWithdrawRecordMapper.deleteBcWithdrawRecordByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除区块链提现记录信息
     * 
     * @param id 区块链提现记录主键
     * @return 结果
     */
    @Override
    public int deleteBcWithdrawRecordById(Long id)
    {
        return bcWithdrawRecordMapper.deleteBcWithdrawRecordById(id);
    }
}
