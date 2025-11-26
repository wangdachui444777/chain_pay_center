package com.ruoyi.blockchain.mapper;

import java.util.List;
import com.ruoyi.blockchain.domain.AddressBalances;
import com.ruoyi.blockchain.domain.UserAddresses;
import org.apache.ibatis.annotations.Param;

/**
 * 地址余额（币种级别）Mapper接口
 * 
 * @author dc
 * @date 2025-10-27
 */
public interface AddressBalancesMapper 
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

    List<AddressBalances> selectBalancesListAfterId(
            @Param("chainType") String chainType,
            @Param("platformId") Long platformId,
            @Param("lastId") Long lastId,
            @Param("batchSize") int batchSize
    );

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
     * 根据地址id修改信息(可能有多个)
     * @param addressBalances
     * @return
     */
    public int updateAddressBalancesByAddressId(AddressBalances addressBalances);
    /**
     * 删除地址余额（币种级别）
     * 
     * @param id 地址余额（币种级别）主键
     * @return 结果
     */
    public int deleteAddressBalancesById(Long id);

    /**
     * 批量删除地址余额（币种级别）
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAddressBalancesByIds(String[] ids);
}
