package com.ruoyi.quartz.task;

import com.ruoyi.bc.component.BlockChainTradeService;
import com.ruoyi.bc.component.ComponentBlockChainService;
import com.ruoyi.bc.service.IBlockChainApiService;
import com.ruoyi.blockchain.domain.*;
import com.ruoyi.blockchain.service.*;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.common.utils.uuid.Seq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * 归集任务
 */
@Component("chainCollectionTask")
public class ChainCollectionTask {

    @Autowired
    private IPlatformsService platformsService;

    @Autowired
    private IBcCollectionConfigService collectionConfigService;

    @Autowired
    private IAddressBalancesService addressBalancesService;

    @Autowired
    private BlockChainTradeService chainTradeService;

    @Autowired
    private IBcConsolidationDetailService consolidationDetailService;

    //step=1 → 发送手续费
    private static final int STEP_SEND_FEE=1;
    //step=2 → 检查手续费
    private static final int STEP_CHECK_FEE=2;
    //step=3 → 发送主交易
    private static final int STEP_SEND_TX=3;
    //step=4 → 检查主交易状态
    private static final int STEP_CHECK_TX=4;

    private static final Logger log = LoggerFactory.getLogger(ChainCollectionTask.class);
    /**
     * 大概凌晨 1点左右处理
     * 一天一次
     */
    public void queryAddress() {

        log.info("开始处理需要归集的地址……");

        List<Platforms> platformList = platformsService.getPlatformsAll();
        List<SysDictData> dictList = DictUtils.getDictCache("bc_chain_type");

        for (Platforms pf : platformList) {

            Long platformId = pf.getId();
            String batchNo = getSeqNo(platformId);

            log.info("开始处理平台 {}，批次号 {}", platformId, batchNo);

            for (SysDictData dict : dictList) {

                String chainType = dict.getDictValue();
                BcCollectionConfig collectionConfig =
                        collectionConfigService.getCollectionConfigCache(platformId, chainType);

                // 自动归集未开启 → 跳过该链
                if (collectionConfig == null || collectionConfig.getEnableAutoCollect() == 0) {
                    log.info("平台 {} 链 {} 未开启归集，跳过", platformId, chainType);
                    continue;
                }

                long lastId = 0L;
                int pageSize = 100;

                log.info("开始扫描平台 {} 的链 {} 地址", platformId, chainType);

                while (true) {

                    // 查询该链分页数据
                    List<AddressBalances> addressBalancesList =
                            addressBalancesService.selectBalancesListAfterId(chainType, platformId, lastId, pageSize);

                    if (addressBalancesList == null || addressBalancesList.isEmpty()) {
                        log.info("平台 {} 链 {} 没有更多地址", platformId, chainType);
                        break;
                    }

                    // 更新 lastId
                    lastId = addressBalancesList.get(addressBalancesList.size() - 1).getId();

                    // 获取需要归集的地址
                    List<BcConsolidationDetail> detailList =
                            addressBalancesService.getCollerctionAddrList(collectionConfig, addressBalancesList, batchNo);

                    if (detailList != null && !detailList.isEmpty()) {
                        consolidationDetailService.batchInsertConsolidationDetail(detailList);
                        log.info("平台 {} 链 {} 插入 {} 条归集记录", platformId, chainType, detailList.size());
                    }
                }
            }
        }

        log.info("处理归集地址 <<全部完成>>");
    }

    private String getSeqNo(Long platformId){
       return DateUtils.dateTime()+platformId;
    }

    /**
     * 给归集需要的地址转入手续费
     * 一天执行1-2次即可
     */
    public void sendGas() {
        checkAllStep("0","0",STEP_SEND_FEE);
    }

    /**
     * 检测手续费状态
     */
    public void checkGasStatus(){
        checkAllStep("1","0",STEP_CHECK_FEE);
    }

    /**
     * 归集
     */
    public void collectAmount(){
        checkAllStep("2","0",STEP_SEND_TX);
    }

    /**
     * 查询归集状态
     */
    public void checkTxStatus(){
        checkAllStep("2","1",STEP_CHECK_TX);
    }

    /**
     * 集中处理所有的步骤
     * @param gasStatus
     * @param txStatus
     * @param step
     */
    private  void checkAllStep(String gasStatus,String txStatus,int step){
        //Gas交易状态：0=未发送, 1=已发送, 2=已确认, 3=失败
        //交易状态：0=未发送, 1=已发送, 2=已确认, 3=失败
        List<String> batchNoList = consolidationDetailService.selectBatchNoByStatus(gasStatus, txStatus);
        if (batchNoList.isEmpty()){
            return;
        }
        List<SysDictData> dictList = DictUtils.getDictCache("bc_chain_type");
        if (dictList==null || dictList.size()<1){
            return;
        }
        BcConsolidationDetail detailQuery=new BcConsolidationDetail();
        detailQuery.setGasTxStatus(gasStatus);
        detailQuery.setTxStatus(txStatus);
        for (String batchNo : batchNoList) {
            //因为批号是日期+平台ID，每次取的都是同平台的
            detailQuery.setBatchNo(batchNo);
            for (SysDictData dict : dictList) {
                String chainType = dict.getDictValue();
                detailQuery.setChainType(chainType);
                List<BcConsolidationDetail> detailList= consolidationDetailService.selectBcConsolidationDetailList(detailQuery);
                switch (step) {
                    case STEP_SEND_FEE :
                        chainTradeService.sendGasFee(chainType,detailList);
                        break;
                    case STEP_CHECK_FEE:
                        chainTradeService.checkGasStatus(chainType, detailList);
                        break;
                    case STEP_SEND_TX:
                        chainTradeService.sendTx(chainType, detailList);
                        break;
                    case STEP_CHECK_TX:
                        chainTradeService.checkTxStatus(chainType, detailList);
                        break;
                    default:
                        break;
                }

            }

        }
    }

}
