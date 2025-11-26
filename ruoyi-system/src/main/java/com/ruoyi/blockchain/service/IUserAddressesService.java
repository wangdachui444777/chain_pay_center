package com.ruoyi.blockchain.service;

import java.util.List;

import com.ruoyi.blockchain.domain.AddressPool;
import com.ruoyi.blockchain.domain.UserAddresses;

/**
 * 用户地址Service接口
 * 
 * @author dc
 * @date 2025-10-27
 */
public interface IUserAddressesService 
{
    /**
     * 查询用户地址
     * 
     * @param id 用户地址主键
     * @return 用户地址
     */
    public UserAddresses selectUserAddressesById(Long id);

    /**
     * 查询用户地址列表
     * 
     * @param userAddresses 用户地址
     * @return 用户地址集合
     */
    public List<UserAddresses> selectUserAddressesList(UserAddresses userAddresses);

    /**
     *
     * @param chainType 区块链 可以为nul
     * @param platformId 平台id 可以为null
     * @param lastId 最近的id，第一次为0
     * @param limit 每次取多少条
     * @return
     */
     List<UserAddresses> selectBatchAfterId(String chainType,Long platformId,Long lastId,int limit);
    /**
     * 新增用户地址
     * 
     * @param userAddresses 用户地址
     * @return 结果
     */
    public int insertUserAddresses(UserAddresses userAddresses);

    /**
     * 初始化地址缓存到Redis
     * 从数据库中加载所有地址到Redis缓存，防止遗漏
     *
     * @param chainType 链类型，如果为null则加载所有链的地址
     */
    void initAddressCache(String chainType);

    int batchInsertUserAddresses(List<AddressPool> addressPools,Long pid,String uuid);
    /**
     * 修改用户地址
     * 
     * @param userAddresses 用户地址
     * @return 结果
     */
    public int updateUserAddresses(UserAddresses userAddresses);

    /**
     * 批量删除用户地址
     * 
     * @param ids 需要删除的用户地址主键集合
     * @return 结果
     */
    public int deleteUserAddressesByIds(String ids);

    /**
     * 删除用户地址信息
     * 
     * @param id 用户地址主键
     * @return 结果
     */
    public int deleteUserAddressesById(Long id);
}
