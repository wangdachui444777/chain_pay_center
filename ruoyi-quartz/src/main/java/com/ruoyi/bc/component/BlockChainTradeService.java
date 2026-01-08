package com.ruoyi.bc.component;

import com.ruoyi.bc.config.ChainTypeConfirmations;
import com.ruoyi.bc.config.Web3jManager;
import com.ruoyi.bc.service.IBlockChainApiService;
import com.ruoyi.bc.service.impl.BlockChainEthApiService;
import com.ruoyi.bc.service.impl.BlockChainTronApiService;
import com.ruoyi.blockchain.domain.*;
import com.ruoyi.blockchain.service.*;
import com.ruoyi.blockchain.service.impl.BlockchainRedisService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.EncryptUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import org.checkerframework.checker.units.qual.A;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class BlockChainTradeService {
    private static final Logger log = LoggerFactory.getLogger(BlockChainTradeService.class);
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
    private IBcFeeSourceAddressesService feeSourceAddressesService;

    @Autowired
    private IBcEnergyPaymentConfigService energyPaymentConfigService;

    @Autowired
    private IBcConsolidationDetailService detailService;

    @Autowired
    private IAddressBalancesService addressBalancesService;

    @Autowired IUserAddressesService userAddressesService;


    /**
     * 提供服务类
     * @param chainType
     * @return
     */
    public IBlockChainApiService bcService(String chainType){
        chainType=chainType.toUpperCase();
        // 防止多线程并发重复初始化
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
     * 处理同一平台，同一个区块链的数据
     * @param chainType
     * @param consolidationDetails
     */
    public void sendGasFee(String chainType, List<BcConsolidationDetail> consolidationDetails){
        if (consolidationDetails.isEmpty()){
            return;
        }
        // 1. 按 from_address 分组
        Map<String, List<BcConsolidationDetail>> groupedByFrom = consolidationDetails.stream()
                // 只处理 ERC20（非主币）代币 和 手续费状态等于0 的
                .filter(d -> !StringUtils.isEmpty(d.getTokenContract()) && "0".equals(d.getGasTxStatus()) )
                .collect(Collectors.groupingBy(BcConsolidationDetail::getFromAddress));

        executor.schedule(execSendGasFee(chainType,groupedByFrom),1, TimeUnit.SECONDS);

    }

    /**
     * 检测发送手续费的状态
     * @param chainType
     * @param consolidationDetails
     */
    public void checkGasStatus(String chainType,List<BcConsolidationDetail> consolidationDetails){
        if (consolidationDetails.isEmpty()){
            return;
        }
        IBlockChainApiService apiService = bcService(chainType);
        //扫描那边最后的块
        Long lastBlockNum = redisService.getNewBlock(chainType);
        if ( lastBlockNum == 0) {
            lastBlockNum = apiService.getLatestBlockNumber();
            redisService.saveNewBlock(chainType,lastBlockNum);
        }
        for (BcConsolidationDetail detail:consolidationDetails) {
            //取gas hash
            if (StringUtils.isEmpty(detail.getGasTxHash())){
                continue;
            }
            String txId=detail.getGasTxHash();
            /**
             * result.put(" success ", false);
             * result.put("blockNumber", null);
             * result.put("gasUsed", 0);
             * result.put("mainCoin", 0);
             */

            Long blockNum=lastBlockNum;
            Long blockNumber=0L;
            boolean status=false;
            if (detail.getGasBlockNumber()==null || detail.getGasBlockNumber()==0L){
                BcEnergyPaymentConfig energyConfig = null;
                if (txId.contains("-")) {
                    energyConfig = energyPaymentConfigService.getEnergyPaymentConfigCache(detail.getPlatformId(), chainType);
                }
                Map<String,Object> obj =apiService.checkTxStatus(txId, energyConfig);
                status=(boolean)obj.get("success");
                if (obj.get("blockNumber")!=null){
                    blockNum=Long.parseLong(obj.get("blockNumber").toString());
                    blockNumber=blockNum;
                }
            }else{
                status=true;
                blockNumber=detail.getGasBlockNumber();
                blockNum=blockNumber;
            }


            BcConsolidationDetail updateData=new BcConsolidationDetail();
            updateData.setId(detail.getId());
            updateData.setBatchNo(detail.getBatchNo());
            updateData.setGasBlockNumber(blockNumber);
            updateData.setAddressId(detail.getAddressId());
            updateData.setId(detail.getId());
            if (!status){
                updateData.setCompleteTime(DateUtils.getNowDate());
                updateData.setStatus("3");
                updateData.setTxStatus("3");
                updateData.setGasTxStatus("3");
                updateData.setErrorMsg("确认手续费失败");
                //改成待处理，等待下次处理
                addressBalancesService.updateStatusByAddressId(detail.getAddressId(),"0");
            }else{
                // 计算确认数
                Integer confirmations = (int) (lastBlockNum - blockNum);
                if (confirmations >= ChainTypeConfirmations.getRequiredConfirmations(chainType)) {
                    updateData.setGasTxStatus("2");
                }
            }
            detailService.updateByMoreWhere(updateData);
        }
    }

    /**
     * 发送归集转账
     * @param chainType
     * @param consolidationDetails
     */
    public void sendTx(String chainType,List<BcConsolidationDetail> consolidationDetails){
        if (consolidationDetails.isEmpty()){
            return;
        }
        IBlockChainApiService apiService = bcService(chainType);
        BigDecimal    gasPrice = apiService.getGasPrice();
        BigDecimal gasLimit=BigDecimal.ONE;
        BigDecimal gasTokenLimit=BigDecimal.ONE;

        for (BcConsolidationDetail detail:consolidationDetails) {
            String fromAddr=detail.getFromAddress();
            String toAddr=detail.getToAddress();
            Long addrId=detail.getAddressId();
            //要查询地址表中的私钥
            UserAddresses userAddresses=userAddressesService.selectUserAddressesById(addrId);
            if (userAddresses==null){
                continue;
            }
            //查询余额表中的余额
           AddressBalances balances= addressBalancesService.selectAddressBalancesById(detail.getAddressBalanceId());
            if (balances==null){
                continue;
            }
            BigDecimal amount=balances.getBalance();
            String fromPrv = EncryptUtils.desDecryption_new(userAddresses.getPrivateKey(), fromAddr);
            if (fromPrv==null){
                continue;
            }
            String txId=null;
            if (StringUtils.isEmpty(detail.getTokenContract())){
                if (gasLimit.compareTo(BigDecimal.ONE)==0) {
                    gasLimit = apiService.getGas(fromAddr, null, "transfer");
                }
                txId= apiService.transfer(fromAddr,fromPrv,toAddr,amount,gasPrice,gasLimit);
            }else{
                String contractAddr=detail.getTokenContract();
                if (gasTokenLimit.compareTo(BigDecimal.ONE)==0) {
                    gasTokenLimit = apiService.getGas(fromAddr, contractAddr, "transfer");
                }
                txId= apiService.transferToken(fromAddr,fromPrv,contractAddr,toAddr,amount,gasPrice,gasTokenLimit);
            }
            // 如果发送失败，更新状态为失败
            String finalStatus = StringUtils.isEmpty(txId) ? "3" : "1";
            //处理数据库
            BcConsolidationDetail update=new BcConsolidationDetail();
            update.setId(detail.getId());
            //交易状态 0=未发送, 1=已发送, 2=已确认, 3=失败
            update.setTxStatus(finalStatus);
            update.setTxHash(txId);
            //归集状态 0=待处理, 1=归集中, 2=已归集,3=失败
            update.setStatus(finalStatus);
            update.setAmount(amount);
            if ("3".equals(finalStatus)){
                update.setCompleteTime(DateUtils.getNowDate());
                update.setErrorMsg("提交转账失败");
                //重置余额表的状态
                addressBalancesService.updateAddressStatus(detail.getAddressBalanceId(),"0");
            }
            detailService.updateBcConsolidationDetail(update);
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 检测交易状态，是否确认
     * @param chainType
     * @param consolidationDetails
     */
    public void checkTxStatus(String chainType,List<BcConsolidationDetail> consolidationDetails){
        if (consolidationDetails.isEmpty()){
            return;
        }
        IBlockChainApiService apiService = bcService(chainType);
        //扫描那边最后的块
        Long lastBlockNum = redisService.getNewBlock(chainType);
        if ( lastBlockNum == 0) {
            lastBlockNum = apiService.getLatestBlockNumber();
            redisService.saveNewBlock(chainType,lastBlockNum);
        }
        for (BcConsolidationDetail detail:consolidationDetails) {
            //取gas hash
            if (StringUtils.isEmpty(detail.getTxHash())){
                continue;
            }
            String txId=detail.getTxHash();
            /**
             * result.put(" success ", false);
             * result.put("blockNumber", null);
             * result.put("gasUsed", 0);
             * result.put("mainCoin", 0);
             */

            Long blockNum=lastBlockNum;
            Long blockNumber=0L;
            boolean status=false;
            BcConsolidationDetail updateData=new BcConsolidationDetail();
            updateData.setId(detail.getId());
            if (detail.getBlockNumber()==null || detail.getBlockNumber()==0L){
                Map<String,Object> obj =apiService.checkTxStatus(txId);
                status=(boolean)obj.get("success");
                if (obj.get("blockNumber")!=null){
                    blockNum=Long.parseLong(obj.get("blockNumber").toString());
                    blockNumber=blockNum;
                }
                BigDecimal gasUsed=new BigDecimal(obj.get("gasUsed").toString());
                updateData.setGasFeeUsed(gasUsed);
            }else{
                status=true;
                blockNumber=detail.getBlockNumber();
                blockNum=blockNumber;
            }
            updateData.setBlockNumber(blockNumber);
            updateData.setRetryCount(detail.getRetryCount()+1);
            String txStatus="1";
            if (!status){
                txStatus="3";
                updateData.setCompleteTime(DateUtils.getNowDate());
                updateData.setErrorMsg("归集失败");
                //改成待处理，等待下次处理
                addressBalancesService.updateStatusByAddressId(detail.getAddressId(),"0");
            }else{
                // 计算确认数
                Integer confirmations = (int) (lastBlockNum - blockNum);
                if (confirmations >= ChainTypeConfirmations.getRequiredConfirmations(chainType)) {
                    TokenPrices prices=null;
                    if (StringUtils.isEmpty(detail.getTokenContract())){
                        prices= tokenPriceService.getTokenCacheByChain(detail.getChainType(),detail.getTokenSymbol());
                    }else{
                        prices= tokenPriceService.getTokenCacheByContractAddress(detail.getTokenContract());
                    }
                    txStatus="2";
                    updateData.setConfirmTime(DateUtils.getNowDate());
                    updateData.setCompleteTime(DateUtils.getNowDate());
                    //更新余额
                    //查询余额表中的余额
                    AddressBalances updateBalances=new AddressBalances();
                    updateBalances.setId(detail.getAddressBalanceId());
                    AddressBalances balances= addressBalancesService.selectAddressBalancesById(detail.getAddressBalanceId());
                    if (balances!=null){
                        BigDecimal tmpBalance=balances.getBalance().subtract(detail.getAmount());
                        updateBalances.setBalance(tmpBalance);
                        updateBalances.setBalanceUsdtValue(tmpBalance.multiply(prices.getPriceInUsdt()));
                    }
                    updateBalances.setStatus("0");
                    //改成待处理，等待下次处理
                    addressBalancesService.updateAddressBalances(updateBalances);
                }
                updateData.setConfirmCount(confirmations.longValue());
            }
            updateData.setTxStatus(txStatus);
            updateData.setStatus(txStatus);
            detailService.updateBcConsolidationDetail(updateData);
        }
    }

    /**
     * 提交提现
     * @param chainType
     * @param platformId
     * @param recordList
     * @param recordService
     */
    public void sendWithdrawTx(String chainType,Long platformId,List<BcWithdrawRecord> recordList,IBcWithdrawRecordService recordService){
        if (recordList.isEmpty()){
            return;
        }
        IBlockChainApiService apiService = bcService(chainType);
        // 手续费-提现支付地址
        BcFeeSourceAddresses sourceAddr = feeSourceAddressesService.getPayAddressCache(platformId, chainType, null);
        if (sourceAddr == null) {
            return; // 或 continue，看业务
        }
        BigDecimal    gasPrice = apiService.getGasPrice();
        BigDecimal gasLimit=BigDecimal.ONE;
        BigDecimal gasTokenLimit=BigDecimal.ONE;

        String fromAddr = sourceAddr.getFeeAddress();
        String fromPrv = EncryptUtils.desDecryption_new(sourceAddr.getPrivateKeyEncrypted(), fromAddr);
        for (BcWithdrawRecord record:recordList){
            String toAddr=record.getToAddress();
            BigDecimal amount=record.getAmount();

            //提现主币
            String txHash=null;
            if (StringUtils.isEmpty(record.getTokenContract())){
                if (gasLimit.compareTo(BigDecimal.ONE)==0) {
                    gasLimit = apiService.getGas(fromAddr, null, "transfer");
                }
                txHash=apiService.transfer(fromAddr,fromPrv,toAddr,amount,gasPrice,gasLimit);
            }else{
                String contractAddr=record.getTokenContract();
                if (gasTokenLimit.compareTo(BigDecimal.ONE)==0) {
                    gasTokenLimit = apiService.getGas(fromAddr, contractAddr, "transfer");
                }
                txHash=apiService.transferToken(fromAddr,fromPrv,contractAddr,toAddr,amount,gasPrice,gasTokenLimit);
            }
            //交易状态：0待提交 1待完成 2已完成 3失败
            BcWithdrawRecord updateData=new BcWithdrawRecord();
            updateData.setId(record.getId());
            updateData.setDetectedTime(DateUtils.getNowDate());
            // 如果发送失败，更新状态为失败
            if (StringUtils.isBlank(txHash)){
                updateData.setTxStatus("3");
            }else{
                updateData.setTxHash(txHash);
                updateData.setTxStatus("1");
            }
            recordService.updateBcWithdrawRecord(updateData);
            try {
                Thread.sleep(400);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * 检测提现交易状态和确认块数
     * @param chainType
     * @param recordList
     * @param recordService
     */
    public void checkWithdrawStatus(String chainType,List<BcWithdrawRecord> recordList,IBcWithdrawRecordService recordService){
        if (recordList.isEmpty()){
            return;
        }
        IBlockChainApiService apiService = bcService(chainType);
        //扫描那边最后的块
        Long lastBlockNum = redisService.getNewBlock(chainType);
        if ( lastBlockNum == 0) {
            lastBlockNum = apiService.getLatestBlockNumber();
            redisService.saveNewBlock(chainType,lastBlockNum);
        }
        for (BcWithdrawRecord record:recordList) {
            //取交易 hash
            if (StringUtils.isEmpty(record.getTxHash())) {
                continue;
            }
            String txId = record.getTxHash();

            /**
             * result.put(" success ", false);
             * result.put("blockNumber", null);
             * result.put("gasUsed", 0);
             * result.put("mainCoin", 0);
             */
            Long blockNum=lastBlockNum;
            Long blockNumber=0L;
            boolean status=false;
            if (record.getBlockNumber()==null || record.getBlockNumber()==0L){
                Map<String,Object> obj =apiService.checkTxStatus(txId);
                status=(boolean)obj.get("success");

                if (obj.get("blockNumber")!=null){
                    blockNum=Long.parseLong(obj.get("blockNumber").toString());
                    blockNumber=blockNum;
                }
            }else{
                status=true;
                blockNumber=record.getBlockNumber();
                blockNum=blockNumber;
            }

            BcWithdrawRecord updateDate=new BcWithdrawRecord();
            updateDate.setId(record.getId());
            updateDate.setBlockNumber(blockNumber);
            // 计算确认数
            Integer confirmations = (int) (lastBlockNum - blockNum);
            if (status){
                if (confirmations >= ChainTypeConfirmations.getRequiredConfirmations(chainType)) {
                    updateDate.setTxStatus("2");
                    updateDate.setConfirmed("1");
                    updateDate.setConfirmations(confirmations.longValue());
                    updateDate.setConfirmedTime(DateUtils.getNowDate());
                }
            }else{
                updateDate.setTxStatus("3");
            }
            recordService.updateBcWithdrawRecord(updateDate);
        }
    }

    /**
     * 异步执行
     * @param chainType
     * @param listMap
     * @return
     */
    private TimerTask execSendGasFee(String chainType, Map<String, List<BcConsolidationDetail>> listMap){
        return new TimerTask() {
            String _chainType=chainType;
            Map<String, List<BcConsolidationDetail>>  detailList =listMap;
            @Override
            public void run() {
                IBlockChainApiService apiService = bcService(_chainType);
                // 租能量标记
                boolean hasEnergy = false;
                BcEnergyPaymentConfig energyConfig = null;
                boolean firstGas=true;

                String payAddress="",payPrivateKey="";
                BigDecimal gasPrice=BigDecimal.ZERO;
                BigDecimal gasLimit=BigDecimal.ZERO;
                BigDecimal fee=BigDecimal.ZERO;
                for (Map.Entry<String, List<BcConsolidationDetail>> entry : detailList.entrySet()) {

                    String toAddress = entry.getKey();
                    List<BcConsolidationDetail> detailList = entry.getValue();
                    Long platformId = detailList.get(0).getPlatformId();
                    Long addressId= detailList.get(0).getAddressId();
                    if(firstGas){
                        energyConfig = energyPaymentConfigService.getEnergyPaymentConfigCache(platformId, _chainType);
                        hasEnergy = energyConfig != null && "0".equals(energyConfig.getStatus());
                        // 手续费支付地址
                        BcFeeSourceAddresses sourceAddr = feeSourceAddressesService.getPayAddressCache(platformId, _chainType, null);
                        if (sourceAddr == null) {
                            break; // 或 continue，看业务
                        }
                        payAddress = sourceAddr.getFeeAddress();
                        payPrivateKey = EncryptUtils.desDecryption_new(sourceAddr.getPrivateKeyEncrypted(), payAddress);
                        // 获取 GasPrice（只调一次）
                        gasPrice = apiService.getGasPrice();
                        // 只取一次 gasLimit（假设同链同代币同方法消耗一致）
                        String contractAddr = detailList.get(0).getTokenContract();

                        gasLimit= apiService.getGas(payAddress, contractAddr, "transfer");


                        fee = apiService.getFee(gasPrice, gasLimit, hasEnergy);

                        firstGas=false;

                    }

                    BigDecimal totalFee = fee.multiply(BigDecimal.valueOf(detailList.size()));

                    // 如果需要转手续费
                    String txId = null;
                    if (totalFee.compareTo(BigDecimal.ZERO) > 0) {
                        txId = apiService.sendTxFee(payAddress, payPrivateKey, toAddress, totalFee, gasPrice, gasLimit, hasEnergy, energyConfig);
                    }
                    // 如果发送失败，更新状态为失败
                    String finalStatus = StringUtils.isEmpty(txId) ? "3" : "1";
                    boolean first = true;
                    // 更新所有同批记录状态
                    for (BcConsolidationDetail dt : detailList) {
                        BcConsolidationDetail upd = new BcConsolidationDetail();
                        upd.setGasTxStatus(finalStatus);
                        if ("3".equals(finalStatus)){
                            upd.setTxStatus("3");
                            upd.setStatus("3");
                            upd.setCompleteTime(DateUtils.getNowDate());
                            upd.setErrorMsg("发送手续费失败");
                        }
                        upd.setId(dt.getId());
                        upd.setGasSendTime(DateUtils.getTimeMillis_10());
                        if (first) {
                            upd.setGasTxHash(txId);
                            upd.setGasFeeSent(totalFee);
                            first = false;
                        }
                        detailService.updateBcConsolidationDetail(upd);

                    }
                    //改成待处理，等待下次处理
                    if ("3".equals(finalStatus)){
                        addressBalancesService.updateStatusByAddressId(addressId,"0");
                    }
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
    }


}
