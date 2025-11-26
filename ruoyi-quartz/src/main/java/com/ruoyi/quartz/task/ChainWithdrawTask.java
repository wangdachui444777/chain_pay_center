package com.ruoyi.quartz.task;

import com.ruoyi.bc.component.BlockChainTradeService;
import com.ruoyi.bc.component.ComponentBlockChainService;
import com.ruoyi.blockchain.domain.BcTransactions;
import com.ruoyi.blockchain.domain.BcWithdrawRecord;
import com.ruoyi.blockchain.domain.Platforms;
import com.ruoyi.blockchain.service.IBcWithdrawRecordService;
import com.ruoyi.blockchain.service.IPlatformsService;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.block.BcSignatureUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import com.ruoyi.quartz.util.CallBackUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component("chainWithdrawTask")
public class ChainWithdrawTask {
    private static final Logger log = LoggerFactory.getLogger(ChainWithdrawTask.class);
    @Autowired
    private IPlatformsService platformsService;
    @Autowired
    private IBcWithdrawRecordService withdrawRecordService;
    @Autowired
    private BlockChainTradeService chainTradeService;

    /**
     * 开始提交提现转账
     * 1分钟左右一次即可
     */
    public void sendTx(){
        log.info("开始处理提现……");
        handle("0","0",1);
        log.info("提现发送<完毕>……");

    }

    /**
     * 30s左右一次
     */
    public void checkConfirmed(){
        log.info("开始确认提现状态……");
        handle("0","1",2);
        log.info("检测提现状态<完毕>……");

    }
    //过期时间10分钟
    private static long TIMEOUT_MILLIS=10 * 60 * 1000;
    /**
     * 30s-1分钟一次
     */
    public void callBack(){
        List<Platforms> platformList = platformsService.getPlatformsAll();
        if (platformList.isEmpty()){
            return;
        }
        BcWithdrawRecord query=new BcWithdrawRecord();
       // query.setConfirmed(confirmStatus);
        //回调状态(1待回调 2回调完成，3失败)
        query.setCallbackStatus("1");
        Date now = DateUtils.getNowDate();
        for (Platforms pf:platformList) {
            query.setPlatformId(pf.getId());
            Platforms platform = platformsService.selectPlatformsById(pf.getId());
            List<BcWithdrawRecord> recordList= withdrawRecordService.selectBcWithdrawRecordList(query);
            for (BcWithdrawRecord record:recordList){
                //交易状态：0待提交 1待完成 2已完成 3失败
                if ("0".equals(record.getTxStatus()) || "1".equals(record.getTxStatus())){
                    Date createTime = record.getCreateTime();
                    if (createTime == null) {
                        log.warn("提现交易ID:{} 检测时间为空，跳过处理", record.getId());
                        continue;
                    }
                    // 计算已经过去的时间
                    long elapsedTime = now.getTime() - createTime.getTime();

                    // 超过5分钟，标记为失败
                    if ( elapsedTime > TIMEOUT_MILLIS) {
                        log.warn("提现交易ID:{} 回调超时，标记为失败。检测时间:{}, 当前时间:{}",
                                record.getId(), createTime, now);
                        updateStatus(record.getId(),"3");
                    }
                    continue;
                }
                handleCallBack(record,platform);
            }
        }

    }

    private void handleCallBack(BcWithdrawRecord record,Platforms platform){

        // 获取平台信息
        if (platform == null || StringUtils.isEmpty(platform.getWithdrawUrl())) {
            log.error("提现交易ID:{} 平台信息不存在或回调地址为空", record.getId());
            updateStatus(record.getId(),"3");
            return;
        }
        // 构建回调参数
        Map<String, Object> callbackData = buildCallbackData(record);
        // 执行回调

        boolean success = CallBackUtils.executeCallback(callbackData, platform);
        if (success) {
            updateStatus(record.getId(),"2");
        } else {
            // 回调失败，不更新状态，等待下次重试
            log.warn("提现交易ID:{} 回调失败，等待重试", record.getId());
        }


    }

    private void updateStatus(Long id,String callStatus){
        BcWithdrawRecord updateDate=new BcWithdrawRecord();
        updateDate.setId(id);
        updateDate.setCallbackStatus(callStatus);
        updateDate.setCallbackTime(DateUtils.getNowDate());
        withdrawRecordService.updateBcWithdrawRecord(updateDate);
    }
    /**
     * 构建回调数据
     */
    private Map<String, Object> buildCallbackData(BcWithdrawRecord record) {

        Map<String, Object> data = CallBackUtils.buildCommonData(
                record.getId(),
                record.getTxHash(),
                record.getTxStatus(),
                record.getChainType(),
                record.getTokenSymbol(),
                record.getTokenContract(),
                record.getAmount(),
                record.getFromAddress(),
                record.getToAddress(),
                record.getConfirmations(),
                record.getConfirmedTime()
        );

        // 特殊字段
        data.put("requestNo", record.getRequestNo());
        data.put("direction", "2");
        data.put("blockNumber", record.getBlockNumber());
        return data;
    }


    private void handle(String confirmStatus,String txStatus,int step){
        List<SysDictData> dictList = DictUtils.getDictCache("bc_chain_type");
        if (dictList==null || dictList.size()<1){
            return;
        }
        List<Platforms> platformList = platformsService.getPlatformsAll();
        if (platformList.isEmpty()){
            return;
        }

        BcWithdrawRecord query=new BcWithdrawRecord();
        query.setConfirmed(confirmStatus);
        query.setTxStatus(txStatus);
        for (Platforms pf:platformList) {
            query.setPlatformId(pf.getId());
            for (SysDictData dict:dictList) {
                String chainType=dict.getDictValue();
                query.setChainType(chainType);
                List<BcWithdrawRecord> recordList= withdrawRecordService.selectBcWithdrawRecordList(query);
                switch (step){
                    case 1:
                        chainTradeService.sendWithdrawTx(chainType,pf.getId(),recordList,withdrawRecordService);
                        break;
                    case 2:
                        chainTradeService.checkWithdrawStatus(chainType,recordList,withdrawRecordService);
                        break;
                    default:
                        break;
                }

            }
        }
    }
}
