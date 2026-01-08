package com.ruoyi.bc.service;

import com.ruoyi.bc.component.ComponentBlockChainService;
import com.ruoyi.blockchain.domain.ApiBcTransaction;
import com.ruoyi.blockchain.domain.BcEnergyPaymentConfig;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface IBlockChainApiService {
    /**
     * 获取最新区块号
     */
    Long getLatestBlockNumber();

    /**
     * 计算所需要的转账主币数量
     * @param gasPrice
     * @param gas
     * @return
     */
     BigDecimal getFee(BigDecimal gasPrice,BigDecimal gas,boolean hasEnergy);

    /**
     * 获取 gas费用
     * @param fromAddr
     * @param contractAddr
     * @param method
     * @return
     */
     BigDecimal getGas(String fromAddr ,String contractAddr,String method);

    /**
     * 检测交易后的状态
     * @param txHash
     * @return result.put(" success ", false);
     *         result.put("blockNumber", null);
     *         result.put("gasUsed", 0);
     *         result.put("mainCoin", 0);
     */
      Map<String, Object> checkTxStatus(String txHash);

    /**
     * 检测交易后的状态（支持第三方能量订单）
     * @param txHash
     * @param energyConfig
     * @return result.put(" success ", false);
     *         result.put("blockNumber", null);
     *         result.put("gasUsed", 0);
     *         result.put("mainCoin", 0);
     */
      Map<String, Object> checkTxStatus(String txHash, com.ruoyi.blockchain.domain.BcEnergyPaymentConfig energyConfig);
    /**
     * 获取gas价格
     * @return
     */
     BigDecimal getGasPrice();

    /**
     * 发送手续费
     * @param fromAddr
     * @param fromPrv
     * @param toAddr
     * @param amount
     * @param gasPrice
     * @param gas
     * @return
     */
    String sendTxFee(String fromAddr,String fromPrv,String toAddr,BigDecimal amount,BigDecimal gasPrice,BigDecimal gas,boolean hasEnergy,BcEnergyPaymentConfig energyConfig);
    /**
     * 主币转账
     * @param fromAddr
     * @param fromPrv
     * @param toAddr
     * @param amount
     * @param gasPrice
     * @param gas
     * @return
     */
     String transfer(String fromAddr,String fromPrv,String toAddr,BigDecimal amount,BigDecimal gasPrice,BigDecimal gas);
    /**
     * 代币转账
     * @param fromAddr
     * @param fromPrv
     * @param contractAddr
     * @param toAddr
     * @param amount
     * @param gasPrice
     * @param gas
     * @return
     */
     String transferToken(String fromAddr,String fromPrv,String contractAddr,String toAddr,BigDecimal amount,BigDecimal gasPrice,BigDecimal gas);

     List<ApiBcTransaction> getBlockTransactions(Long startBlockNum, Long endBlockNum, ComponentBlockChainService handlerService);


}
