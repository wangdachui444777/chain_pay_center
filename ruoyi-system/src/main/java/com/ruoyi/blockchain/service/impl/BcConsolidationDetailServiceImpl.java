package com.ruoyi.blockchain.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.blockchain.mapper.BcConsolidationDetailMapper;
import com.ruoyi.blockchain.domain.BcConsolidationDetail;
import com.ruoyi.blockchain.service.IBcConsolidationDetailService;
import com.ruoyi.common.core.text.Convert;

/**
 * 归集记录Service业务层处理
 * 
 * @author dc
 * @date 2025-11-09
 */
@Service
public class BcConsolidationDetailServiceImpl implements IBcConsolidationDetailService 
{
    @Autowired
    private BcConsolidationDetailMapper bcConsolidationDetailMapper;

    /**
     * 查询归集记录
     * 
     * @param id 归集记录主键
     * @return 归集记录
     */
    @Override
    public BcConsolidationDetail selectBcConsolidationDetailById(Long id)
    {
        return bcConsolidationDetailMapper.selectBcConsolidationDetailById(id);
    }

    /**
     * 查询归集记录列表
     * 
     * @param bcConsolidationDetail 归集记录
     * @return 归集记录
     */
    @Override
    public List<BcConsolidationDetail> selectBcConsolidationDetailList(BcConsolidationDetail bcConsolidationDetail)
    {
        return bcConsolidationDetailMapper.selectBcConsolidationDetailList(bcConsolidationDetail);
    }
    /**
     * 查询批次号列表（去重）
     * @param gasTxStatus
     * @param txStatus
     * @return
     */
    public List<String> selectBatchNoByStatus(String gasTxStatus,String txStatus){
        return bcConsolidationDetailMapper.selectBatchNoByStatus(gasTxStatus,txStatus);
    }


    /**
     * 根据批次号查询代币需要转手续费的列表
     * @param batchNo
     * @return
     */
    public List<BcConsolidationDetail> getGasListByBatchNo(String batchNo,String chainType){
        return bcConsolidationDetailMapper.selectGasListByBatchNo(batchNo,chainType);
    }

    /**
     * 新增归集记录
     * 
     * @param bcConsolidationDetail 归集记录
     * @return 结果
     */
    @Override
    public int insertBcConsolidationDetail(BcConsolidationDetail bcConsolidationDetail)
    {
        bcConsolidationDetail.setCreateTime(DateUtils.getNowDate());
        return bcConsolidationDetailMapper.insertBcConsolidationDetail(bcConsolidationDetail);
    }
    /**
     * 批量初始化数据
     * @param list
     * @return
     */
    public int batchInsertConsolidationDetail(@Param("list") List<BcConsolidationDetail> list){

        if (list.isEmpty()){
            return 0;
        }
        return bcConsolidationDetailMapper.batchInsertConsolidationDetail(list);
    }

    /**
     * 修改归集记录
     * 
     * @param bcConsolidationDetail 归集记录
     * @return 结果
     */
    @Override
    public int updateBcConsolidationDetail(BcConsolidationDetail bcConsolidationDetail)
    {
        bcConsolidationDetail.setUpdateTime(DateUtils.getNowDate());
        return bcConsolidationDetailMapper.updateBcConsolidationDetail(bcConsolidationDetail);
    }
    /**
     * 多个组合条件更新，可能会是多条
     * @param bcConsolidationDetail
     * @return
     */
    public int updateByMoreWhere(BcConsolidationDetail bcConsolidationDetail){
        bcConsolidationDetail.setUpdateTime(DateUtils.getNowDate());
       return bcConsolidationDetailMapper.updateByBatchNoAndAddrId(bcConsolidationDetail);
    }



    /**
     * 批量删除归集记录
     * 
     * @param ids 需要删除的归集记录主键
     * @return 结果
     */
    @Override
    public int deleteBcConsolidationDetailByIds(String ids)
    {
        return bcConsolidationDetailMapper.deleteBcConsolidationDetailByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除归集记录信息
     * 
     * @param id 归集记录主键
     * @return 结果
     */
    @Override
    public int deleteBcConsolidationDetailById(Long id)
    {
        return bcConsolidationDetailMapper.deleteBcConsolidationDetailById(id);
    }
}
