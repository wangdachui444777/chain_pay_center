package com.ruoyi.blockchain.domain;

import java.math.BigDecimal;

/**
 * TRON 交易模型
 */

public class EthTransaction extends ApiBcTransaction {

    /** 天然气消耗 */
    private Long gasUsed;

    /** 燃气价格 */
    private Long gasPrice;

    /** 手续费 */
    private BigDecimal txFee;

    public Long getGasUsed() {
        return gasUsed;
    }

    public void setGasUsed(Long gasUsed) {
        this.gasUsed = gasUsed;
    }

    public Long getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(Long gasPrice) {
        this.gasPrice = gasPrice;
    }

    public BigDecimal getTxFee() {
        return txFee;
    }

    public void setTxFee(BigDecimal txFee) {
        this.txFee = txFee;
    }
}
