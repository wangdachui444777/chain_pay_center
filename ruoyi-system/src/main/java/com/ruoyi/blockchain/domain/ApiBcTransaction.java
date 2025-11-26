package com.ruoyi.blockchain.domain;

import java.math.BigDecimal;

public class ApiBcTransaction {
    /** 交易哈希 */
    private String txHash;

    /** 区块号 */
    private Long blockNumber;

    /** 区块时间戳 */
    private Long blockTimestamp;

    /** 发送方地址 */
    private String fromAddress;

    /** 接收方地址 */
    private String toAddress;

    /** 交易金额 */
    private BigDecimal amount;

    /** 币种符号 */
    private String tokenSymbol;

    /** 代币合约地址（主币为空） */
    private String tokenContract;

    /** 代币精度 */
    private Integer tokenDecimals;

    /** 交易类型：TRX / TRC20 */
    private String txType;

    /** 交易状态：SUCCESS / FAILED */
    private String status;

    /** 合约调用结果 */
    private Boolean contractResult;

    /** 是否确认 */
    private String confirmed;



    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public Long getBlockTimestamp() {
        return blockTimestamp;
    }

    public void setBlockTimestamp(Long blockTimestamp) {
        this.blockTimestamp = blockTimestamp;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTokenSymbol() {
        return tokenSymbol;
    }

    public void setTokenSymbol(String tokenSymbol) {
        this.tokenSymbol = tokenSymbol;
    }

    public String getTokenContract() {
        return tokenContract;
    }

    public void setTokenContract(String tokenContract) {
        this.tokenContract = tokenContract;
    }

    public Integer getTokenDecimals() {
        return tokenDecimals;
    }

    public void setTokenDecimals(Integer tokenDecimals) {
        this.tokenDecimals = tokenDecimals;
    }

    public String getTxType() {
        return txType;
    }

    public void setTxType(String txType) {
        this.txType = txType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getContractResult() {
        return contractResult;
    }

    public void setContractResult(Boolean contractResult) {
        this.contractResult = contractResult;
    }

    public String getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(String confirmed) {
        this.confirmed = confirmed;
    }
}
