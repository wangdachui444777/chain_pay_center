package com.ruoyi.blockchain.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 区块链提现记录对象 bc_withdraw_record
 * 
 * @author dc
 * @date 2025-11-12
 */
public class BcWithdrawRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号 */
    private Long id;

    /** 平台ID */
    @Excel(name = "平台ID")
    private Long platformId;

    /** 下游单号 */
    @Excel(name = "下游单号")
    private String requestNo;

    /** 区块链 */
    @Excel(name = "区块链")
    private String chainType;

    /** 币种符号 */
    @Excel(name = "币种符号")
    private String tokenSymbol;

    /** 合约地址 */
    private String tokenContract;

    /** 付款地址 */
    private String fromAddress;

    /** 收款地址 */
    @Excel(name = "收款地址")
    private String toAddress;

    /** 提现金额 */
    @Excel(name = "提现金额")
    private BigDecimal amount;

    /** 交易哈希 */
    @Excel(name = "交易哈希")
    private String txHash;

    private Long blockNumber;

    /** 确认状态 */
    @Excel(name = "确认状态")
    private String confirmed;

    /** 确认数 */
    @Excel(name = "确认数")
    private Long confirmations;

    /** 交易状态 */
    @Excel(name = "交易状态")
    private String txStatus;

    /** 交易时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "交易时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date detectedTime;

    /** 确认时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "确认时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date confirmedTime;

    /** 回调状态 */
    @Excel(name = "回调状态")
    private String callbackStatus;

    /** 回调完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "回调完成时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date callbackTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setPlatformId(Long platformId) 
    {
        this.platformId = platformId;
    }

    public Long getPlatformId() 
    {
        return platformId;
    }

    public void setRequestNo(String requestNo) 
    {
        this.requestNo = requestNo;
    }

    public String getRequestNo() 
    {
        return requestNo;
    }

    public void setChainType(String chainType) 
    {
        this.chainType = chainType;
    }

    public String getChainType() 
    {
        return chainType;
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

    public void setAmount(BigDecimal amount) 
    {
        this.amount = amount;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }

    public void setTxHash(String txHash) 
    {
        this.txHash = txHash;
    }

    public String getTxHash() 
    {
        return txHash;
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public void setConfirmed(String confirmed)
    {
        this.confirmed = confirmed;
    }

    public String getConfirmed() 
    {
        return confirmed;
    }

    public void setConfirmations(Long confirmations) 
    {
        this.confirmations = confirmations;
    }

    public Long getConfirmations() 
    {
        return confirmations;
    }

    public void setTxStatus(String txStatus) 
    {
        this.txStatus = txStatus;
    }

    public String getTxStatus() 
    {
        return txStatus;
    }

    public void setDetectedTime(Date detectedTime) 
    {
        this.detectedTime = detectedTime;
    }

    public Date getDetectedTime() 
    {
        return detectedTime;
    }

    public void setConfirmedTime(Date confirmedTime) 
    {
        this.confirmedTime = confirmedTime;
    }

    public Date getConfirmedTime() 
    {
        return confirmedTime;
    }

    public void setCallbackStatus(String callbackStatus) 
    {
        this.callbackStatus = callbackStatus;
    }

    public String getCallbackStatus() 
    {
        return callbackStatus;
    }

    public void setCallbackTime(Date callbackTime) 
    {
        this.callbackTime = callbackTime;
    }

    public Date getCallbackTime() 
    {
        return callbackTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("platformId", getPlatformId())
            .append("requestNo", getRequestNo())
            .append("chainType", getChainType())
            .append("tokenSymbol", getTokenSymbol())
            .append("tokenContract", getTokenContract())
            .append("fromAddress", getFromAddress())
            .append("toAddress", getToAddress())
            .append("amount", getAmount())
            .append("txHash", getTxHash())
            .append("confirmed", getConfirmed())
            .append("confirmations", getConfirmations())
            .append("txStatus", getTxStatus())
            .append("detectedTime", getDetectedTime())
            .append("confirmedTime", getConfirmedTime())
            .append("callbackStatus", getCallbackStatus())
            .append("callbackTime", getCallbackTime())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
