package com.ruoyi.blockchain.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 归集记录对象 bc_consolidation_detail
 * 
 * @author dc
 * @date 2025-11-09
 */
public class BcConsolidationDetail extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 批次号 */
    @Excel(name = "批次号")
    private String batchNo;

    /** 平台ID */
    @Excel(name = "平台ID")
    private Long platformId;

    /** 地址ID */
    @Excel(name = "地址余额ID")
    private Long addressBalanceId;


    /** 地址ID */
    @Excel(name = "地址ID")
    private Long addressId;


    /** 链类型 */
    @Excel(name = "链类型")
    private String chainType;

    /** 源地址 */
    @Excel(name = "源地址")
    private String fromAddress;

    /** 目标地址 */
    @Excel(name = "目标地址")
    private String toAddress;

    /** 代币符号 */
    @Excel(name = "代币符号")
    private String tokenSymbol;

    /** 合约地址 */
    @Excel(name = "合约地址")
    private String tokenContract;

    /** 归集金额 */
    @Excel(name = "归集金额")
    private BigDecimal amount;

    /** Gas费用 */
    @Excel(name = "Gas费用")
    private BigDecimal gasFeeSent;

    /** 使用的Gas */
    @Excel(name = "使用的Gas")
    private BigDecimal gasFeeUsed;

    /** Gas交易哈希 */
    @Excel(name = "Gas交易哈希")
    private String gasTxHash;
    /**
     * 手续费区块号
     */
    private Long gasBlockNumber;
    /** Gas交易状态：0=未发送, 1=已发送, 2=已确认, 3=失败 */
    @Excel(name = "Gas交易状态：0=未发送, 1=已发送, 2=已确认, 3=失败")
    private String gasTxStatus;

    /** 发送手续费时间 */
    @Excel(name = "发送手续费时间")
    private Long gasSendTime;

    /** 交易哈希 */
    @Excel(name = "交易哈希")
    private String txHash;
    /**
     * 交易区块号
     */
    private Long blockNumber;
    /** 交易状态 */
    @Excel(name = "交易状态")
    private String txStatus;

    /** 确认数 */
    @Excel(name = "确认数")
    private Long confirmCount;

    /** 确认时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date confirmTime;

    /** 重试次数 */
    @Excel(name = "重试次数")
    private Long retryCount;

    /** 错误信息 */
    @Excel(name = "错误信息")
    private String errorMsg;

    /** 整体状态 */
    @Excel(name = "整体状态")
    private String status;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startTime;

    /** 完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "完成时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date completeTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setBatchNo(String batchNo) 
    {
        this.batchNo = batchNo;
    }

    public String getBatchNo() 
    {
        return batchNo;
    }

    public void setPlatformId(Long platformId) 
    {
        this.platformId = platformId;
    }

    public Long getPlatformId() 
    {
        return platformId;
    }

    public Long getAddressBalanceId() {
        return addressBalanceId;
    }

    public void setAddressBalanceId(Long addressBalanceId) {
        this.addressBalanceId = addressBalanceId;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public void setChainType(String chainType)
    {
        this.chainType = chainType;
    }

    public String getChainType() 
    {
        return chainType;
    }

    public void setFromAddress(String fromAddress) 
    {
        this.fromAddress = fromAddress;
    }

    public String getFromAddress() 
    {
        return fromAddress;
    }

    public void setToAddress(String toAddress) 
    {
        this.toAddress = toAddress;
    }

    public String getToAddress() 
    {
        return toAddress;
    }

    public void setTokenSymbol(String tokenSymbol) 
    {
        this.tokenSymbol = tokenSymbol;
    }

    public String getTokenSymbol() 
    {
        return tokenSymbol;
    }

    public void setTokenContract(String tokenContract) 
    {
        this.tokenContract = tokenContract;
    }

    public String getTokenContract() 
    {
        return tokenContract;
    }

    public void setAmount(BigDecimal amount) 
    {
        this.amount = amount;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }

    public void setGasFeeSent(BigDecimal gasFeeSent) 
    {
        this.gasFeeSent = gasFeeSent;
    }

    public BigDecimal getGasFeeSent() 
    {
        return gasFeeSent;
    }

    public void setGasFeeUsed(BigDecimal gasFeeUsed) 
    {
        this.gasFeeUsed = gasFeeUsed;
    }

    public BigDecimal getGasFeeUsed() 
    {
        return gasFeeUsed;
    }

    public void setGasTxHash(String gasTxHash) 
    {
        this.gasTxHash = gasTxHash;
    }

    public String getGasTxHash() 
    {
        return gasTxHash;
    }

    public void setGasTxStatus(String gasTxStatus) 
    {
        this.gasTxStatus = gasTxStatus;
    }

    public String getGasTxStatus() 
    {
        return gasTxStatus;
    }

    public void setGasSendTime(Long gasSendTime) 
    {
        this.gasSendTime = gasSendTime;
    }

    public Long getGasSendTime() 
    {
        return gasSendTime;
    }

    public void setTxHash(String txHash) 
    {
        this.txHash = txHash;
    }

    public String getTxHash() 
    {
        return txHash;
    }

    public void setTxStatus(String txStatus) 
    {
        this.txStatus = txStatus;
    }

    public String getTxStatus() 
    {
        return txStatus;
    }

    public void setConfirmCount(Long confirmCount) 
    {
        this.confirmCount = confirmCount;
    }

    public Long getConfirmCount() 
    {
        return confirmCount;
    }

    public void setConfirmTime(Date confirmTime) 
    {
        this.confirmTime = confirmTime;
    }

    public Date getConfirmTime() 
    {
        return confirmTime;
    }

    public void setRetryCount(Long retryCount) 
    {
        this.retryCount = retryCount;
    }

    public Long getRetryCount() 
    {
        return retryCount;
    }

    public void setErrorMsg(String errorMsg) 
    {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg() 
    {
        return errorMsg;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setStartTime(Date startTime) 
    {
        this.startTime = startTime;
    }

    public Date getStartTime() 
    {
        return startTime;
    }

    public void setCompleteTime(Date completeTime) 
    {
        this.completeTime = completeTime;
    }

    public Date getCompleteTime() 
    {
        return completeTime;
    }

    public Long getGasBlockNumber() {
        return gasBlockNumber;
    }

    public void setGasBlockNumber(Long gasBlockNumber) {
        this.gasBlockNumber = gasBlockNumber;
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }

    @Override
    public String toString() {
        return "BcConsolidationDetail{" +
                "id=" + id +
                ", batchNo='" + batchNo + '\'' +
                ", platformId=" + platformId +
                ", addressBalanceId=" + addressBalanceId +
                ", addressId=" + addressId +
                ", chainType='" + chainType + '\'' +
                ", fromAddress='" + fromAddress + '\'' +
                ", toAddress='" + toAddress + '\'' +
                ", tokenSymbol='" + tokenSymbol + '\'' +
                ", tokenContract='" + tokenContract + '\'' +
                ", amount=" + amount +
                ", gasFeeSent=" + gasFeeSent +
                ", gasFeeUsed=" + gasFeeUsed +
                ", gasTxHash='" + gasTxHash + '\'' +
                ", gasBlockNumber=" + gasBlockNumber +
                ", gasTxStatus='" + gasTxStatus + '\'' +
                ", gasSendTime=" + gasSendTime +
                ", txHash='" + txHash + '\'' +
                ", blockNumber=" + blockNumber +
                ", txStatus='" + txStatus + '\'' +
                ", confirmCount=" + confirmCount +
                ", confirmTime=" + confirmTime +
                ", retryCount=" + retryCount +
                ", errorMsg='" + errorMsg + '\'' +
                ", status='" + status + '\'' +
                ", startTime=" + startTime +
                ", completeTime=" + completeTime +
                '}';
    }
}
