package com.ruoyi.blockchain.mapper;

import java.util.List;
import com.ruoyi.blockchain.domain.BcConsolidationDetail;
import org.apache.ibatis.annotations.Param;

/**
 * 归集记录Mapper接口
 * 
 * @author dc
 * @date 2025-11-09
 */
public interface BcConsolidationDetailMapper 
{
    /**
     * 查询归集记录
     * 
     * @param id 归集记录主键
     * @return 归集记录
     */
    public BcConsolidationDetail selectBcConsolidationDetailById(Long id);


    /**
     * 查询批次号列表（去重）
     * @param gasTxStatus
     * @param txStatus
     * @return
     */
    List<String> selectBatchNoByStatus(@Param("gasTxStatus") String gasTxStatus,@Param("txStatus") String txStatus);

    /**
     * 根据批次号查询代币需要转手续费的列表
     * @param batchNo
     * @return
     */
    List<BcConsolidationDetail> selectGasListByBatchNo(@Param("batchNo") String batchNo,@Param("chainType") String ChainType);


    /**
     * 查询归集记录列表
     * 
     * @param bcConsolidationDetail 归集记录
     * @return 归集记录集合
     */
    public List<BcConsolidationDetail> selectBcConsolidationDetailList(BcConsolidationDetail bcConsolidationDetail);

    /**
     * 新增归集记录
     * 
     * @param bcConsolidationDetail 归集记录
     * @return 结果
     */
    public int insertBcConsolidationDetail(BcConsolidationDetail bcConsolidationDetail);

    /**
     * 批量初始化数据
     * @param list
     * @return
     */
    int batchInsertConsolidationDetail(@Param("list") List<BcConsolidationDetail> list);
    /**
     * 修改归集记录
     * 
     * @param bcConsolidationDetail 归集记录
     * @return 结果
     */
    public int updateBcConsolidationDetail(BcConsolidationDetail bcConsolidationDetail);

    /**
     * 多个组合条件更新，可能会是多条
     * @param bcConsolidationDetail
     * @return
     */
    int updateByBatchNoAndAddrId(BcConsolidationDetail bcConsolidationDetail);
    /**
     * 删除归集记录
     * 
     * @param id 归集记录主键
     * @return 结果
     */
    public int deleteBcConsolidationDetailById(Long id);

    /**
     * 批量删除归集记录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBcConsolidationDetailByIds(String[] ids);
}
