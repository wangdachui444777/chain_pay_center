package com.ruoyi.blockchain.service.impl;

import java.util.*;

import com.github.pagehelper.PageHelper;
import com.ruoyi.blockchain.domain.AddressPool;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.blockchain.mapper.UserAddressesMapper;
import com.ruoyi.blockchain.domain.UserAddresses;
import com.ruoyi.blockchain.service.IUserAddressesService;
import com.ruoyi.common.core.text.Convert;

/**
 * 用户地址Service业务层处理
 * 
 * @author dc
 * @date 2025-10-27
 */
@Service
public class UserAddressesServiceImpl implements IUserAddressesService 
{
    @Autowired
    private UserAddressesMapper userAddressesMapper;

    @Autowired
    private  BlockchainRedisService blockchainRedisService;

    /**
     * 查询用户地址
     * 
     * @param id 用户地址主键
     * @return 用户地址
     */
    @Override
    public UserAddresses selectUserAddressesById(Long id)
    {

        return userAddressesMapper.selectUserAddressesById(id);
    }

    /**
     * 查询用户最后的地址
     *
     * @return 用户地址
     */
    public UserAddresses selectUserAddressesMaxId()
    {

        return userAddressesMapper.selectUserAddressesMaxId();
    }

    /**
     * 查询用户地址列表
     * 
     * @param userAddresses 用户地址
     * @return 用户地址
     */
    @Override
    public List<UserAddresses> selectUserAddressesList(UserAddresses userAddresses)
    {
        return userAddressesMapper.selectUserAddressesList(userAddresses);
    }

    /**
     *
     * @param chainType 区块链 可以为nul
     * @param platformId 平台id 可以为null
     * @param lastId 最近的id，第一次为0
     * @param limit 每次取多少条
     * @return
     */
    public List<UserAddresses> selectBatchAfterId(String chainType,Long platformId,Long lastId,int limit){
        return userAddressesMapper.selectBatchAfterId(chainType,platformId,lastId,limit);

    }

    /**
     * 新增用户地址
     * 
     * @param userAddresses 用户地址
     * @return 结果
     */
    @Override
    public int insertUserAddresses(UserAddresses userAddresses)
    {
        userAddresses.setCreateTime(DateUtils.getNowDate());
        return userAddressesMapper.insertUserAddresses(userAddresses);
    }

    /**
     * 把临时表的数据插入用户地址表
     * @param addressPools
     * @return
     */
    public int batchInsertUserAddresses(List<AddressPool> addressPools,Long pid,String uuid) {
        if (StringUtils.isEmpty(uuid)) {
            uuid = IdUtils.simpleUUID();
        }
        int count=0;
        for (AddressPool pool : addressPools) {
            UserAddresses ua = new UserAddresses();
            ua.setPlatformId(pid);
            ua.setUserId(uuid);
            ua.setChainType(pool.getChainType());
            ua.setAddress(pool.getAddress());
            ua.setPrivateKey(pool.getPrivateKey());
            ua.setCreateTime(DateUtils.getNowDate());
            int flag=this.insertUserAddresses(ua);
            count+=flag;
            // 获取自增ID（MyBatis 自动回填）
            Long addressId = ua.getId();
            // 写入 Redis 监听缓存
            blockchainRedisService.addMonitorAddress(pool.getChainType(),pool.getAddress(),addressId);

        }
        return count;
    }
    /**
     * 初始化地址缓存到Redis
     * 从数据库中加载所有地址到Redis缓存，防止遗漏
     *
     * @param chainType 链类型，如果为null则加载所有链的地址
     */
    public void initAddressCache(String chainType) {
        int pageSize = 2000; // 每批处理数量
        Long lastId = 0L;
        try {
            UserAddresses lastUserAddresses =this.selectUserAddressesMaxId();
            if (lastUserAddresses==null){
                return;
            }
            // 检查Redis中是否已存在
            Boolean exists = blockchainRedisService.isMonitored(chainType, lastUserAddresses.getAddress());
            if (Boolean.TRUE.equals(exists)) {
                return;
            }

            while (true) {
               /** // 查询条件
                UserAddresses query = new UserAddresses();
                // 分页查询数据库
                PageHelper.orderBy("id asc");
                PageHelper.offsetPage(pageNum , pageSize);
                if (StringUtils.isNotEmpty(chainType)) {
                    query.setChainType(chainType);
                }**/
                // 从数据库查询所有地址
                List<UserAddresses> addressList = userAddressesMapper.selectBatchAfterId(chainType,null,lastId,pageSize);

                if (addressList == null || addressList.isEmpty()) {
                    break; // 没有更多数据
                }
                // 遍历地址列表，加载到Redis
                for (UserAddresses address : addressList) {
                    // 检查Redis中是否已存在
                    exists = blockchainRedisService.isMonitored(chainType, address.getAddress());

                    if (Boolean.FALSE.equals(exists)) {
                        // 写入Redis缓存
                        blockchainRedisService.addMonitorAddress(chainType, address.getAddress(), address.getId());
                        // addCount++;
                    }
                    lastId=address.getId();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 修改用户地址
     * 
     * @param userAddresses 用户地址
     * @return 结果
     */
    @Override
    public int updateUserAddresses(UserAddresses userAddresses)
    {
        userAddresses.setUpdateTime(DateUtils.getNowDate());
        return userAddressesMapper.updateUserAddresses(userAddresses);
    }

    /**
     * 批量删除用户地址
     * 
     * @param ids 需要删除的用户地址主键
     * @return 结果
     */
    @Override
    public int deleteUserAddressesByIds(String ids)
    {
        return userAddressesMapper.deleteUserAddressesByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除用户地址信息
     * 
     * @param id 用户地址主键
     * @return 结果
     */
    @Override
    public int deleteUserAddressesById(Long id)
    {
        return userAddressesMapper.deleteUserAddressesById(id);
    }
}
