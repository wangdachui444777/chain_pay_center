package com.ruoyi.quartz.task;


import com.ruoyi.bc.component.ComponentBlockChainService;

import com.ruoyi.blockchain.service.IUserAddressesService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * TRON 区块链监听器
 * 支持 TRX 主币和 TRC20 代币（USDT、USDC）监听
 */
@Component("scanTronChainTask")
public class ScanTronChainTask {
    private static final Logger log = LoggerFactory.getLogger(ScanTronChainTask.class);

    /**
     * 每次扫描区块数量
     */
    private Integer scanBlockCount = 5;

    @Autowired
    private IUserAddressesService userAddressesService;

    @Autowired
    private ComponentBlockChainService blockChainService;

    /**
     * 是否正在扫描
     */
    private volatile boolean isScanning = false;

    /**
     * TRON链类型标识
     */
    private static final String CHAIN_TYPE = "TRX";

    @PostConstruct
    public void init() {
        // 初始化TRON地址缓存
        userAddressesService.initAddressCache(CHAIN_TYPE);
        log.info("========================================");
        log.info("{} 监听器初始化完成",CHAIN_TYPE);
        log.info("========================================");
    }
    /**
     * 定时扫描 TRON 区块
     * 扫描间隔由配置文件控制（默认10秒）
     */
    public void scanTronBlocks(Integer blockCount) {
        // 防止并发扫描
        if (isScanning) {
            log.debug("上次扫描未完成，跳过本次");
            return;
        }
        if (blockCount != null) {
            this.scanBlockCount = blockCount;
        }
        isScanning = true;
        try {
            log.info("开始扫描 {} 区块...",CHAIN_TYPE);
            blockChainService.scanTronBlocks(CHAIN_TYPE,scanBlockCount);
            log.info("<<<TRON 扫描完成>>>");

        } catch (Exception e) {
            log.error(" {}  扫描异常",CHAIN_TYPE, e);
        } finally {
            isScanning = false;
        }
    }
    /**
     * 定时扫描 TRON 区块
     * 扫描间隔由配置文件控制（默认10秒）
     */
    public void updatePending(){
        blockChainService.updatePending(CHAIN_TYPE);
    }


}
