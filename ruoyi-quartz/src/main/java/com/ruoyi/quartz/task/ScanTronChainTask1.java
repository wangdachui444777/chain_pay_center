package com.ruoyi.quartz.task;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.bc.config.ChainTypeConfirmations;
import com.ruoyi.bc.service.impl.TronScanApiService;
import com.ruoyi.blockchain.domain.BcTransactions;
import com.ruoyi.blockchain.domain.TronTransaction;
import com.ruoyi.blockchain.service.IAddressBalancesService;
import com.ruoyi.blockchain.service.IBcTransactionsService;
import com.ruoyi.blockchain.service.IUserAddressesService;
import com.ruoyi.blockchain.service.impl.BlockchainRedisService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * TRON 区块链监听器
 * 支持 TRX 主币和 TRC20 代币（USDT、USDC）监听
 */
@Component("scanTronChainTask1")
public class ScanTronChainTask1 {
    private static final Logger log = LoggerFactory.getLogger(ScanTronChainTask1.class);
    /**
     * 异步操作任务调度线程池
     */
    private ScheduledExecutorService executor = SpringUtils.getBean("scheduledExecutorService");

    /**
     * 每次扫描区块数量
     */
    private Integer scanBlockCount = 10;
    @Autowired
    private TronScanApiService tronScanApiService;

    @Autowired
    private BlockchainRedisService redisService;

    @Autowired
    private IBcTransactionsService bcTransactionsService;

    @Autowired
    private IAddressBalancesService addressBalancesService;

    @Autowired
    private IUserAddressesService userAddressesService;

    /**
     * 是否正在扫描
     */
    private volatile boolean isScanning = false;

    /**
     * TRON链类型标识
     */
    private static final String CHAIN_TYPE_TRON = "TRX";


    /** TRON 平均出块时间(毫秒) */
    private static final long BLOCK_TIME_MS = 3000;

    private static  int CONFIRMATION_BLOCKS=19;

    @PostConstruct
    public void init() {
        // 初始化TRON地址缓存
        userAddressesService.initAddressCache(CHAIN_TYPE_TRON);
        log.info("========================================");
        log.info("TRON 监听器初始化完成");
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
            log.info("🔍 开始扫描 TRON 区块...");

            // 1. 获取当前最新区块
            Long latestBlock = tronScanApiService.getLatestBlockNumber();
            if (latestBlock == null || latestBlock == 0) {
                log.warn("获取最新区块失败");
                return;
            }

            // 2. 获取上次扫描的区块
            Long lastScannedBlock = redisService.getLatestBlock(CHAIN_TYPE_TRON);
            if (lastScannedBlock == 0) {
                // 首次扫描，从最新块往前10个开始
                lastScannedBlock = latestBlock - scanBlockCount;
                log.info("首次扫描，从区块 {} 开始", lastScannedBlock);
            }
            // 3. 计算扫描范围（留出确认块避免分叉）
            CONFIRMATION_BLOCKS=ChainTypeConfirmations.getRequiredConfirmations(CHAIN_TYPE_TRON);
            Long safeBlockNumber = latestBlock -CONFIRMATION_BLOCKS ;

            if (lastScannedBlock >= safeBlockNumber) {
                log.info("已扫描到最新区块，等待新区块产生...");
                return;
            }

            // 3. 计算需要扫描的区块范围
            long startBlock = lastScannedBlock + 1;
            long endBlock = Math.min(startBlock + scanBlockCount - 1, safeBlockNumber);
            log.info("扫描区块范围: {} - {} (链上最新: {})", startBlock, endBlock, latestBlock);

            // 4. 逐个扫描区块
            // 6. 按区块扫描交易
            int totalProcessed = 0;
            for (Long blockNumber = startBlock; blockNumber <= endBlock; blockNumber++) {
                try {
                    int count = scanBlockRange(blockNumber);
                    redisService.saveLatestBlock(CHAIN_TYPE_TRON, blockNumber);
                    totalProcessed += count;
                    if (count > 0) {
                        log.info("区块 {} 处理 {} 笔交易", blockNumber, count);
                    }
                    // 每10个区块休息一下
                    if (totalProcessed % 10 == 0) {
                        Thread.sleep(100);
                    }

                } catch (Exception e) {
                    log.error("扫描区块 {} 失败", blockNumber, e);
                }
            }

            log.info("TRON 扫描完成: 区块 {} -> {}", startBlock, endBlock);

        } catch (Exception e) {
            log.error(" TRON 扫描异常", e);
        } finally {
            isScanning = false;
        }
    }

    /**
     * 扫描指定区块范围的交易
     */
    private int scanBlockRange(Long blockNumber) {
        int totalProcessed = 0;
        int offset = 0;
        int limit = 50; //不能超过50
        boolean hasMore = true;


        while (hasMore) {
            try {
                log.debug("扫描区块 {} limit{}  offset={}", blockNumber,limit, offset);
                JSONObject jsonObject=tronScanApiService.getBlockByBock(limit,offset,blockNumber);
                // 获取交易列表
                JSONArray transactions = jsonObject.getJSONArray("data");
                if (transactions == null || transactions.isEmpty()) {
                    hasMore = false;
                    break;
                }

                // 获取总数
                Integer total = jsonObject.getInteger("total");
                List<TronTransaction> tronTransactions =tronScanApiService.parseBlockTransactions(jsonObject);
                if (tronTransactions.isEmpty()) {
                    //log.debug("区块 {} 无交易", blockNumber);
                    continue;
                }
                // 3. 处理每笔交易
                executor.schedule(processTransaction(tronTransactions), 0, TimeUnit.SECONDS);
                // 分页控制
                offset += limit;

                // 判断是否还有更多数据
                if (total != null && offset >= total) {
                    hasMore = false;
                }

                if (transactions.size() < limit) {
                    hasMore = false;
                }

            } catch (Exception e) {
                log.error("扫描区块范围 {} 出错: offset={}", blockNumber, offset, e);
                hasMore = false;
            }
        }

        return totalProcessed;
    }



    /**
     * 异步处理
     * 处理单笔交易
     */
    private TimerTask processTransaction(List<TronTransaction> list) {
        return new TimerTask() {
            @Override
            public void run() {
                int matchedCount = 0;
                List<TronTransaction> transactionList=list;
                for (TronTransaction tx : transactionList) {
                    try {
                        String toAddress = tx.getToAddress();
                        // 1. 检查是否是监听地址
                        Long addressId = redisService.getAddressId(CHAIN_TYPE_TRON, toAddress);
                        if (addressId == null) {
                            //log.debug("地址 {} 不在监听列表", toAddress);
                            continue;
                        }
                        // 2. 防止重复处理
                        if (!redisService.tryLockTransaction(tx.getTxHash(), 30)) {
                            // log.debug("交易 {} 正在处理中", tx.getTxHash());
                            continue;
                        }

                            // 4. 构建交易记录
                            log.info("═══════════════════════════════════════");
                            log.info("发现充值交易！");
                            log.info("交易哈希: {}", tx.getTxHash());
                            log.info("form: {}", tx.getFromAddress());
                            log.info("to: {}", tx.getToAddress());
                            log.info("币种: {} ({})", tx.getTokenSymbol(), tx.getTxType());
                            log.info("金额: {}", tx.getAmount());
                            if (tx.getTokenContract() != null) {
                                log.info("合约: {}", tx.getTokenContract());
                            }
                            log.info("═══════════════════════════════════════");
                            // 5. 保存到数据库
                            boolean saved = bcTransactionsService.saveBcTransactions(CHAIN_TYPE_TRON, addressId, tx);

                            if (saved) {
                                log.info("交易入库成功: {}", tx.getTxHash());
                            }
                            ++matchedCount;

                    } catch (Exception e) {
                        log.error("处理交易 {} 异常", tx.getTxHash(), e);
                    }finally {
                        // 释放锁
                       // redisService.unlockTransaction(tx.getTxHash());
                    }
                }
                log.info("发现 {} 笔相关交易", matchedCount);
            }
        };

    }

    /**
     * 定时更新未确认交易的确认数（每30秒）
     * 更新完顺便更新usdt 余额
     */
    public void updatePending() {
        try {
            log.debug("开始更新未确认交易...");
            String chainType = CHAIN_TYPE_TRON;
            Long currentBlock = redisService.getLatestBlock(chainType);
            if (currentBlock == 0) {
                currentBlock = tronScanApiService.getLatestBlockNumber();
            }
            // 1. 查询未确认的交易
            BcTransactions query = new BcTransactions();
            query.setChainType(chainType);
            query.setConfirmed("0");
            query.setTxStatus("2");
            //回调状态(1待回调 2回调完成，3失败)
            //query.setCallbackStatus("1");
            //1待完成，2已完成，3失败
            //
            List<BcTransactions> pendingTxs = bcTransactionsService.selectBcTransactionsList(query);
            if (pendingTxs.isEmpty()) {
                return;
            }

            int updatedCount = 0;
            for (BcTransactions tx : pendingTxs) {
                // 计算确认数
                Integer confirmations = (int) (currentBlock - tx.getBlockNumber());

                if (confirmations >= ChainTypeConfirmations.getRequiredConfirmations(chainType)) {
                    // 达到确认数，更新状态
                    tx.setConfirmations(confirmations.longValue());
                    tx.setConfirmed("1");
                    tx.setConfirmedTime(DateUtils.getNowDate());
                    bcTransactionsService.updateBcTransactions(tx);
                    updatedCount++;
                    String tokenContract=null;
                    if (tx.getTokenContract() != null) {
                        tokenContract=tx.getTokenContract();
                    }
                    //6. 更新地址余额
                    addressBalancesService.updateOrSaveBalances(tx.getAddressId(),
                            tx.getChainType(),
                            tx.getTokenSymbol(),
                            tx.getAmount(),
                            tokenContract,tx.getPlatformId());

                    log.info("交易已确认: {} ({} 确认)", tx.getTxHash(), confirmations);
                }
            }
            if (updatedCount > 0) {
                log.info("更新了 {} 笔已确认交易", updatedCount);
            }

        } catch (Exception e) {
            log.error("更新未确认交易异常", e);
        }
    }
}
