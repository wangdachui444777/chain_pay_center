package com.ruoyi.blockchain.domain;

import com.ruoyi.common.annotation.Excel;

import java.math.BigDecimal;

/**
 * TRON 交易模型
 */

public class TronTransaction extends ApiBcTransaction {

    /** 能量消耗 */
    private Long energyUsed;

    /** 带宽消耗 */
    private Long netUsed;


    public Long getEnergyUsed() {
        return energyUsed;
    }

    public void setEnergyUsed(Long energyUsed) {
        this.energyUsed = energyUsed;
    }

    public Long getNetUsed() {
        return netUsed;
    }

    public void setNetUsed(Long netUsed) {
        this.netUsed = netUsed;
    }


}
