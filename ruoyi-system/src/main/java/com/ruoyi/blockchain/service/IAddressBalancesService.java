package com.ruoyi.blockchain.service;

import java.math.BigDecimal;
import java.util.List;
import com.ruoyi.blockchain.domain.AddressBalances;
import com.ruoyi.blockchain.domain.BcCollectionConfig;
import com.ruoyi.blockchain.domain.BcConsolidationDetail;
import com.ruoyi.blockchain.domain.UserAddresses;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.bean.BeanUtils;

/**
 * 地址余额（币种级别）Service接口
 * 
 * @author dc
 * @date 2025-10-27
 */
public interface IAddressBalancesService 
{
    /**
     * 查询地址余额（币种级别）
     * 
     * @param id 地址余额（币种级别）主键
     * @return 地址余额（币种级别）
     */
    public AddressBalances selectAddressBalancesById(Long id);

    /**
     * 查询地址余额（币种级别）列表
     * 
     * @param addressBalances 地址余额（币种级别）
     * @return 地址余额（币种级别）集合
     */
    public List<AddressBalances> selectAddressBalancesList(AddressBalances addressBalances);

    /**
     *
     * @param chainType
     * @param platformId
     * @param lastId
     * @param batchSize
     * @return
     */
    List<AddressBalances> selectBalancesListAfterId(String chainType,Long platformId,Long lastId, int batchSize);
    /**
     * 获取要归集的数据
     * @param collectionConfig 归集配置
     * @param balancesList 用户地址
     * @return
     */
    public  List<BcConsolidationDetail> getCollerctionAddrList(BcCollectionConfig collectionConfig, List<AddressBalances> balancesList, String batchNo);
    /**
     * 新增地址余额（币种级别）
     * 
     * @param addressBalances 地址余额（币种级别）
     * @return 结果
     */
    public int insertAddressBalances(AddressBalances addressBalances);

    /**
     * 修改地址余额（币种级别）
     * 
     * @param addressBalances 地址余额（币种级别）
     * @return 结果
     */
    public int updateAddressBalances(AddressBalances addressBalances);


    /**
     * 修改状态
     * @param id
     * @param status
     * @return
     */
     int updateAddressStatus(Long id,String status);

    /**
     * 根据地址ID修改状态(多个)
     * @param addressId
     * @param status
     * @return
     */
     int updateStatusByAddressId(Long addressId,String status);
    /**
     * 新增或者更新余额
     * 顺便更新usdt价格
     * @param addressId
     * @param ChainType
     * @param tokenSymbol
     * @param amount
     * @return
     */
    boolean updateOrSaveBalances(Long addressId, String ChainType, String tokenSymbol, BigDecimal amount,String tokenContract,Long pId);
    /**
     * 批量删除地址余额（币种级别）
     * 
     * @param ids 需要删除的地址余额（币种级别）主键集合
     * @return 结果
     */
    public int deleteAddressBalancesByIds(String ids);

    /**
     * 删除地址余额（币种级别）信息
     * 
     * @param id 地址余额（币种级别）主键
     * @return 结果
     */
    public int deleteAddressBalancesById(Long id);
}
