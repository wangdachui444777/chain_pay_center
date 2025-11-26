package com.ruoyi.bc.component;

import com.ruoyi.bc.config.ChainTypeConfirmations;
import com.ruoyi.bc.config.Web3jManager;
import com.ruoyi.bc.service.IBlockChainApiService;
import com.ruoyi.bc.service.impl.BlockChainEthApiService;
import com.ruoyi.bc.service.impl.BlockChainTronApiService;
import com.ruoyi.blockchain.domain.ApiBcTransaction;
import com.ruoyi.blockchain.domain.BcTransactions;
import com.ruoyi.blockchain.service.IAddressBalancesService;
import com.ruoyi.blockchain.service.IBcApiKeysService;
import com.ruoyi.blockchain.service.IBcTransactionsService;
import com.ruoyi.blockchain.service.ITokenPricesService;
import com.ruoyi.blockchain.service.impl.BlockchainRedisService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class ComponentBlockChainService {
    private static final Logger log = LoggerFactory.getLogger(ComponentBlockChainService.class);

    /**
     * 异步操作任务调度线程池
     */
    private ScheduledExecutorService executor = SpringUtils.getBean("scheduledExecutorService");

    @Autowired
    private ITokenPricesService tokenPriceService;
    @Autowired
    private IBcApiKeysService apiKeyService;
    @Autowired
    private BlockchainRedisService redisService;
    @Autowired
    private Web3jManager web3jManager;


    @Autowired
    private IBcTransactionsService bcTransactionsService;
    @Autowired
    private IAddressBalancesService addressBalancesService;

    //线程安全
    static Map<String,Boolean> _mapIsScanning=new ConcurrentHashMap<>();
    /**
     * 提供服务类
     * @param chainType
     * @return
     */
    public IBlockChainApiService bcService(String chainType){
        chainType=chainType.toUpperCase();
        // 防止多线程并发重复初始化
        _mapIsScanning.putIfAbsent(chainType, false);
        switch (chainType){
            case "ETH":
                return BlockChainEthApiService.getInstance(tokenPriceService,redisService,web3jManager);
            case "TRX":
                return BlockChainTronApiService.getInstance(tokenPriceService,apiKeyService);
            default:
                return null;
        }
    }

    /**
     * 定时扫描 TRON 区块
     * 扫描间隔由配置文件控制（默认10秒）
     */
    public void scanTronBlocks(String chainType,Integer blockCount) {
        chainType=chainType.toUpperCase();
        IBlockChainApiService blockChainApiService=bcService(chainType);

        // 防止并发扫描
        if (_mapIsScanning.get(chainType)) {
            log.debug("上次扫描未完成，跳过本次");
            return;
        }
        _mapIsScanning.put(chainType,true);
        try {
           // log.info("开始扫描 {} 区块...",chainType);
            // 1. 获取当前最新区块
            Long latestBlock = blockChainApiService.getLatestBlockNumber();
            if (latestBlock == null || latestBlock == 0) {
                log.warn("获取最新区块失败");
                return;
            }
            // 2. 获取上次扫描的区块
            Long lastScannedBlock = redisService.getLatestBlock(chainType);
            if (lastScannedBlock == 0) {
                // 首次扫描，从最新块往前10个开始
                lastScannedBlock = latestBlock - blockCount;
                log.info("首次扫描，从区块 {} 开始", lastScannedBlock);
            }
            // 3. 计算需要扫描的区块范围
            long startBlock = lastScannedBlock;
            long endBlock = Math.min(startBlock + blockCount, latestBlock);

            if (startBlock >= latestBlock) {
                log.info("无新区块，当前: {}", latestBlock);
                return;
            }
            log.info("扫描区块范围: {} -> {} (最新: {})", startBlock, endBlock, latestBlock);

            /**
            // 4. 逐个扫描区块
            for (long blockNum = startBlock; blockNum <= endBlock; blockNum++) {
                int matched = scanSingleBlock(blockNum);
                // 更新已扫描的区块
                redisService.saveLatestBlock(CHAIN_TYPE_TRON, blockNum);
                Thread.sleep(100);
            }**/
            //多个块扫描
            blockChainApiService.getBlockTransactions(startBlock,endBlock,this);

            //记录最后的块
            redisService.saveLatestBlock(chainType, endBlock);
            redisService.saveNewBlock(chainType,latestBlock);
            log.info(" {} 扫描完成: 区块 {} -> {}",chainType, startBlock, endBlock);

        } catch (Exception e) {
            log.error(" {} 扫描异常",chainType, e);
        } finally {
            _mapIsScanning.put(chainType,false);
        }
    }

    /**
     * 定时更新未确认交易的确认数（每30秒）
     * 更新完顺便更新usdt 余额
     */
    public void updatePending(String chainType) {
        try {
            chainType=chainType.toUpperCase();
            IBlockChainApiService blockChainApiService=this.bcService(chainType);
            log.debug("开始更新未确认交易...");
            Long currentBlock = redisService.getNewBlock(chainType);
            if ( currentBlock == 0) {
                currentBlock = blockChainApiService.getLatestBlockNumber();
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
                    //6. 更新地址余额（可选）
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

    public void HandlerSaveTransaction(List<ApiBcTransaction> bcTransactionList,String chainType){
        if (bcTransactionList.isEmpty()){
            return;
        }
        executor.schedule(processTransaction(bcTransactionList,chainType), 1, TimeUnit.SECONDS);
    }
    /**
     * 异步处理
     * 处理单笔交易
     */
    private TimerTask processTransaction(List<ApiBcTransaction> list,String chainType) {
        return new TimerTask() {
            @Override
            public void run() {
                int matchedCount = 0;
                List<ApiBcTransaction> transactionList=list;
                String _chainType=chainType;
                for (ApiBcTransaction tx : transactionList) {
                    try {
                        String toAddress = tx.getToAddress();
                        // 1. 检查是否是监听地址
                        Long addressId = redisService.getAddressId(_chainType, toAddress);
                        if (addressId == null) {
                            //log.debug("地址 {} 不在监听列表", toAddress);
                            continue;
                        }
                        // 2. 防止重复处理
                        if (!redisService.tryLockTransaction(tx.getTxHash(), 10)) {
                            // log.debug("交易 {} 正在处理中", tx.getTxHash());
                            continue;
                        }
                        // 4. 构建交易记录
                        log.info("═══════════════════════════════════════");
                        log.info("发现充值交易！");
                        log.info("交易哈希: {}", tx.getTxHash());
                        log.info("币种: {} ({})", tx.getTokenSymbol(), tx.getTxType());
                        log.info("金额: {}", tx.getAmount());
                        if (tx.getTokenContract() != null) {
                            log.info("合约: {}", tx.getTokenContract());
                        }
                        log.info("═══════════════════════════════════════");
                        // 5. 保存到数据库
                        boolean saved = bcTransactionsService.saveBcTransactions(_chainType, addressId, tx);

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
                log.info("保存有效 {} 笔相关交易", matchedCount);
            }
        };

    }

}
