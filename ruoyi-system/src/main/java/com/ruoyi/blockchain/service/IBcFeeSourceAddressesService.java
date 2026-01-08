package com.ruoyi.blockchain.service;

import java.util.List;
import com.ruoyi.blockchain.domain.BcFeeSourceAddresses;

/**
 * 付款地址Service接口
 * 
 * @author dc
 * @date 2025-11-09
 */
public interface IBcFeeSourceAddressesService 
{
    /**
     * 查询付款地址
     * 
     * @param id 付款地址主键
     * @return 付款地址
     */
    public BcFeeSourceAddresses selectBcFeeSourceAddressesById(Long id);

    /**
     * 查询付款地址列表
     * 
     * @param bcFeeSourceAddresses 付款地址
     * @return 付款地址集合
     */
    public List<BcFeeSourceAddresses> selectBcFeeSourceAddressesList(BcFeeSourceAddresses bcFeeSourceAddresses);

    /**
     * 新增付款地址
     * 
     * @param bcFeeSourceAddresses 付款地址
     * @return 结果
     */
    public int insertBcFeeSourceAddresses(BcFeeSourceAddresses bcFeeSourceAddresses);

    /**
     * 修改付款地址
     * 
     * @param bcFeeSourceAddresses 付款地址
     * @return 结果
     */
    public int updateBcFeeSourceAddresses(BcFeeSourceAddresses bcFeeSourceAddresses);

    /**
     * 获取缓存
     * @param platformId
     * @param chainType
     * @param address 可以为null，为null时随机取
     * @return
     */
     BcFeeSourceAddresses getPayAddressCache(Long platformId,String chainType,String address);

    /**
     * 批量删除付款地址
     * 
     * @param ids 需要删除的付款地址主键集合
     * @return 结果
     */
    public int deleteBcFeeSourceAddressesByIds(String ids);

    /**
     * 删除付款地址信息
     * 
     * @param id 付款地址主键
     * @return 结果
     */
    public int deleteBcFeeSourceAddressesById(Long id);
}
