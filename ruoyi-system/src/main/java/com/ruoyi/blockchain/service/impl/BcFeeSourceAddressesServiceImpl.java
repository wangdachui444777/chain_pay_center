package com.ruoyi.blockchain.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import com.ruoyi.blockchain.domain.BcApiKeys;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.EncryptUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.ruoyi.blockchain.mapper.BcFeeSourceAddressesMapper;
import com.ruoyi.blockchain.domain.BcFeeSourceAddresses;
import com.ruoyi.blockchain.service.IBcFeeSourceAddressesService;
import com.ruoyi.common.core.text.Convert;

/**
 * 付款地址Service业务层处理
 * 
 * @author dc
 * @date 2025-11-09
 */
@Service
public class BcFeeSourceAddressesServiceImpl implements IBcFeeSourceAddressesService 
{
    @Autowired
    private BcFeeSourceAddressesMapper bcFeeSourceAddressesMapper;

    @Autowired
    private RedisCache redisCache;

    /** 币种配置 */
    public static final String PAY_FEE_PREFIX = "bc:fee:address:";
    /**
     * 查询付款地址
     * 
     * @param id 付款地址主键
     * @return 付款地址
     */
    @Override
    public BcFeeSourceAddresses selectBcFeeSourceAddressesById(Long id)
    {
        return bcFeeSourceAddressesMapper.selectBcFeeSourceAddressesById(id);
    }

    /**
     * 查询付款地址列表
     * 
     * @param bcFeeSourceAddresses 付款地址
     * @return 付款地址
     */
    @Override
    public List<BcFeeSourceAddresses> selectBcFeeSourceAddressesList(BcFeeSourceAddresses bcFeeSourceAddresses)
    {
        return bcFeeSourceAddressesMapper.selectBcFeeSourceAddressesList(bcFeeSourceAddresses);
    }

    /**
     * 新增付款地址
     * 
     * @param bcFeeSourceAddresses 付款地址
     * @return 结果
     */
    @Override
    public int insertBcFeeSourceAddresses(BcFeeSourceAddresses bcFeeSourceAddresses)
    {
        bcFeeSourceAddresses.setCreateTime(DateUtils.getNowDate());
        // 保存数据库时加密私钥（用地址做盐值）
        String privateKey=bcFeeSourceAddresses.getPrivateKeyEncrypted();
        String address=bcFeeSourceAddresses.getFeeAddress();
        bcFeeSourceAddresses.setPrivateKeyEncrypted(EncryptUtils.desEncryption_new(privateKey, address));
        int flag= bcFeeSourceAddressesMapper.insertBcFeeSourceAddresses(bcFeeSourceAddresses);
        if (flag>0){
            setCache(bcFeeSourceAddresses);
        }
        return flag;
    }

    /**
     * 修改付款地址
     * 
     * @param bcFeeSourceAddresses 付款地址
     * @return 结果
     */
    @Override
    public int updateBcFeeSourceAddresses(BcFeeSourceAddresses bcFeeSourceAddresses)
    {
        BcFeeSourceAddresses sourceAddresses= selectBcFeeSourceAddressesById(bcFeeSourceAddresses.getId());
        if (!sourceAddresses.getPrivateKeyEncrypted().equals(bcFeeSourceAddresses.getPrivateKeyEncrypted())) {
            // 保存数据库时加密私钥（用地址做盐值）
            String privateKey=bcFeeSourceAddresses.getPrivateKeyEncrypted();
            String address=bcFeeSourceAddresses.getFeeAddress();
            bcFeeSourceAddresses.setPrivateKeyEncrypted(EncryptUtils.desEncryption_new(privateKey, address));
        }


        bcFeeSourceAddresses.setUpdateTime(DateUtils.getNowDate());
        int flag= bcFeeSourceAddressesMapper.updateBcFeeSourceAddresses(bcFeeSourceAddresses);
        if (flag>0){
            setCache(bcFeeSourceAddresses);
        }
        return flag;
    }

    /**
     * 设置缓存
     * @param sourceAddresses
     */
    private void setCache(BcFeeSourceAddresses sourceAddresses){
        String key=getKey(sourceAddresses.getPlatformId(),sourceAddresses.getChainType(),sourceAddresses.getFeeAddress());
        if ("0".equals(sourceAddresses.getStatus())){
           redisCache.setCacheObject(key,sourceAddresses);
        }else{
            redisCache.deleteObject(key);
        }

    }

    /**
     * 获取缓存
     *
     * @param platformId 平台ID
     * @param chainType  链类型
     * @param address    具体地址，可为 null；为 null 时默认取一个地址
     */
    public BcFeeSourceAddresses getPayAddressCache(Long platformId, String chainType, String address) {
        // 1. 优先从缓存获取（address 为空时，内部 getCache 自己决定如何处理）
        BcFeeSourceAddresses addressInfo = getCache(platformId, chainType, address);
        if (addressInfo != null) {
            return addressInfo;
        }

        // 2. 缓存没有，查询数据库
        BcFeeSourceAddresses query = new BcFeeSourceAddresses();
        query.setPlatformId(platformId);
        query.setChainType(chainType);
        query.setStatus("0");   // 只取可用的

        List<BcFeeSourceAddresses> list = selectBcFeeSourceAddressesList(query);
        if (list == null || list.isEmpty()) {
            return null; // 没有可用地址
        }

        // 3. 刷新缓存：对该平台+链的所有地址都 setCache 一遍
        boolean addressIsBlank = StringUtils.isBlank(address);
        for (BcFeeSourceAddresses item : list) {
            // 覆盖写入缓存，相当于“全部重置”
            setCache(item);
            if (!addressIsBlank && address.equalsIgnoreCase(item.getFeeAddress())) {
                return item;
            }
        }

        // 4. 未传 address 时随机取一个；传了 address 未命中则返回 null
        if (addressIsBlank) {
            return list.get(ThreadLocalRandom.current().nextInt(list.size()));
        }
        return null;
    }


    private BcFeeSourceAddresses getCache(Long platformId,String chainType,String address){
        String key=getKey(platformId,chainType,address);
        if (StringUtils.isEmpty(address)){
            List<BcFeeSourceAddresses> list=new ArrayList<>();
            Set<String> keys= (Set<String>) redisCache.keys(key);
            if (keys == null || keys.isEmpty()) {
                return null;
            }
            for (String k : keys) {
                BcFeeSourceAddresses v=redisCache.getCacheObject(k);
                if (v != null) {
                    list.add(v);
                }
            }

            if (!list.isEmpty()) {
                return list.get(ThreadLocalRandom.current().nextInt(list.size()));
            }
            return null;
        }
        return redisCache.getCacheObject(key);
    }
    private String getKey(Long platformId,String chainType,String address){
        String key=PAY_FEE_PREFIX+platformId+":"+chainType;
        if (StringUtils.isEmpty(address)){
            key=key+":*";
        }else{
            key=key+":"+address;
        }
        return key.toLowerCase();
    }
    private void clearCache(){
        Collection<String> cacheKeys = redisCache.keys(PAY_FEE_PREFIX + "*");
        redisCache.deleteObject(cacheKeys);
    }
    /**
     * 批量删除付款地址
     * 
     * @param ids 需要删除的付款地址主键
     * @return 结果
     */
    @Override
    public int deleteBcFeeSourceAddressesByIds(String ids)
    {
        clearCache();
        return bcFeeSourceAddressesMapper.deleteBcFeeSourceAddressesByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除付款地址信息
     * 
     * @param id 付款地址主键
     * @return 结果
     */
    @Override
    public int deleteBcFeeSourceAddressesById(Long id)
    {
        clearCache();
        return bcFeeSourceAddressesMapper.deleteBcFeeSourceAddressesById(id);
    }
}
