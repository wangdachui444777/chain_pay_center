package com.ruoyi.blockchain.mapper;

import java.util.List;
import com.ruoyi.blockchain.domain.UserAddresses;
import org.apache.ibatis.annotations.Param;

/**
 * 用户地址Mapper接口
 * 
 * @author dc
 * @date 2025-10-27
 */
public interface UserAddressesMapper 
{
    /**
     * 查询用户地址
     * 
     * @param id 用户地址主键
     * @return 用户地址
     */
    public UserAddresses selectUserAddressesById(Long id);

    /**
     * 获取最后一条用户地址
     * @return
     */
    UserAddresses selectUserAddressesMaxId();
    /**
     * 查询用户地址列表
     * 
     * @param userAddresses 用户地址
     * @return 用户地址集合
     */
    public List<UserAddresses> selectUserAddressesList(UserAddresses userAddresses);

    /**
     * 按ID递增游标分页查询地址
     * @param chainType 链类型（可选）
     * @param lastId 上一次查询的最后ID（初始为0）
     * @param batchSize 每批数量（建议1000-5000）
     * @return 地址列表
     */
    List<UserAddresses> selectBatchAfterId(
            @Param("chainType") String chainType,
            @Param("platformId") Long platformId,
            @Param("lastId") Long lastId,
            @Param("batchSize") int batchSize
    );
    /**
     * 新增用户地址
     * 
     * @param userAddresses 用户地址
     * @return 结果
     */
    public int insertUserAddresses(UserAddresses userAddresses);


    /**
     * 修改用户地址
     * 
     * @param userAddresses 用户地址
     * @return 结果
     */
    public int updateUserAddresses(UserAddresses userAddresses);

    /**
     * 批量插入用户地址
     * @param userAddresses
     * @return
     */
     int batchInsert(List<UserAddresses> userAddresses);
    /**
     * 删除用户地址
     * 
     * @param id 用户地址主键
     * @return 结果
     */
    public int deleteUserAddressesById(Long id);

    /**
     * 批量删除用户地址
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteUserAddressesByIds(String[] ids);
}
