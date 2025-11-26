package com.ruoyi.blockchain.service.impl;



import com.ruoyi.common.core.redis.RedisCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 区块链 Redis 缓存服务
 */
@Service
public class BlockchainRedisService {
    private static final Logger log = LoggerFactory.getLogger(BlockchainRedisService.class);

    @Autowired
    private RedisTemplate redisTemplate;

    /** 地址监听前缀 */
    public static final String ADDRESS_PREFIX = "bc:address:";

    /** 最后区块前缀 */
    private static final String LATEST_BLOCK_PREFIX = "blockchain:block:latest:";

    /** 最新区块前缀 */
    private static final String NEW_BLOCK_PREFIX = "blockchain:block:new:";

    /** 交易处理锁前缀 */
    private static final String TX_LOCK_PREFIX = "blockchain:tx:lock:";



    /**
     * 添加监听地址
     * @param chainType 链类型
     * @param address 地址
     * @param addressId 地址ID
     */
    public void addMonitorAddress(String chainType, String address, Long addressId) {
        chainType=chainType.toLowerCase();
        address=address.toLowerCase();
        String key = ADDRESS_PREFIX + chainType + ":" + address;
        redisTemplate.opsForValue().set(key, addressId);
        log.info("添加监听地址：{} -> {}", key, addressId);
    }

    /**
     * 获取地址ID
     */
    public Long getAddressId(String chainType, String address) {
        chainType=chainType.toLowerCase();
        address=address.toLowerCase();
        String key = ADDRESS_PREFIX + chainType + ":" + address;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value.toString()) : null;
    }

    /**
     * 检查地址是否被监听
     */
    public boolean isMonitored(String chainType, String address) {
        chainType=chainType.toLowerCase();
        address=address.toLowerCase();
        String key = ADDRESS_PREFIX + chainType + ":" + address;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 获取所有监听地址
     */
    public Set<String> getAllMonitoredAddresses(String chainType) {
        chainType=chainType.toLowerCase();
        String pattern = ADDRESS_PREFIX + chainType + ":*";
        return redisTemplate.keys(pattern);
    }
    /**
     * 从Redis缓存中移除地址
     *
     * @param chainType 链类型
     * @param address 地址
     */
    public void removeAddressFromCache(String chainType, String address) {
        chainType=chainType.toLowerCase();
        address=address.toLowerCase();
        String key = ADDRESS_PREFIX + chainType + ":" + address;
        Boolean deleted = redisTemplate.delete(key);
    }
    /**
     * 清空指定链类型的地址缓存
     *
     * @param chainType 链类型
     */
    public void clearAddressCache(String chainType) {
        chainType=chainType.toLowerCase();
        try {
            String pattern = ADDRESS_PREFIX + chainType + ":*";
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                 redisTemplate.delete(keys);
            }

        } catch (Exception e) {
        }
    }

    /**
     * 保存最后区块高度
     */
    public void saveLatestBlock(String chainType, Long blockNumber) {
        String key = LATEST_BLOCK_PREFIX + chainType;
        redisTemplate.opsForValue().set(key, blockNumber);
    }

    /**
     * 获取最后区块高度
     */
    public Long getLatestBlock(String chainType) {
        String key = LATEST_BLOCK_PREFIX + chainType;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value.toString()) : 0L;
    }
    /**
     * 保存最新区块高度
     */
    public void saveNewBlock(String chainType, Long blockNumber) {
        String key = NEW_BLOCK_PREFIX + chainType;
        redisTemplate.opsForValue().set(key, blockNumber);
    }

    /**
     * 获取最新区块高度
     */
    public Long getNewBlock(String chainType) {
        String key = NEW_BLOCK_PREFIX + chainType;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? Long.parseLong(value.toString()) : 0L;
    }

    /**
     * 尝试获取交易处理锁（防止重复处理）
     */
    public boolean tryLockTransaction(String txHash, long seconds) {
        String key = TX_LOCK_PREFIX + txHash;
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, "1", seconds, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(result);
    }

    /**
     * 释放交易锁
     */
    public void unlockTransaction(String txHash) {
        String key = TX_LOCK_PREFIX + txHash;
        redisTemplate.delete(key);
    }
}