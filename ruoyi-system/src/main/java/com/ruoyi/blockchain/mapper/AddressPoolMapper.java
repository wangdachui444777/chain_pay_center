package com.ruoyi.blockchain.mapper;

import java.util.List;
import com.ruoyi.blockchain.domain.AddressPool;

/**
 * 临时地址Mapper接口
 * 
 * @author dc
 * @date 2025-10-28
 */
public interface AddressPoolMapper 
{
    /**
     * 查询临时地址
     * 
     * @param id 临时地址主键
     * @return 临时地址
     */
    public AddressPool selectAddressPoolById(Long id);

    /**
     * 查询临时地址列表
     * 
     * @param addressPool 临时地址
     * @return 临时地址集合
     */
    public List<AddressPool> selectAddressPoolList(AddressPool addressPool);

    /**
     * 根据链类型批量查询
     */
    AddressPool selectByChainTypes(String chainType);


    /**
     * 根据链类型统计未分配地址数量
     */
    int countByChainType(String chainType);

    /**
     * 批量插入地址池
     */
    int batchInsertAddressPool(List<AddressPool> addressList);

    /**
     * 新增临时地址
     * 
     * @param addressPool 临时地址
     * @return 结果
     */
    public int insertAddressPool(AddressPool addressPool);

    /**
     * 修改临时地址
     * 
     * @param addressPool 临时地址
     * @return 结果
     */
    public int updateAddressPool(AddressPool addressPool);

    /**
     * 删除临时地址
     * 
     * @param id 临时地址主键
     * @return 结果
     */
    public int deleteAddressPoolById(Long id);

    /**
     * 批量删除临时地址
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteAddressPoolByIds(String[] ids);
}
