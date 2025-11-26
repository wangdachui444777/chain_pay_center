package com.ruoyi.blockchain.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 地址充值记录对象 bc_transactions
 * 
 * @author dc
 * @date 2025-10-30
 */
public class BcTransactions extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long id;

    /** 关联地址ID（bc_user_addresses.id） */
    @Excel(name = "关联地址ID", readConverterExp = "b=c_user_addresses.id")
    private Long addressId;

    /** 所属平台ID */
    @Excel(name = "所属平台ID")
    private Long platformId;

    /** 交易哈希 */
    @Excel(name = "交易哈希")
    private String txHash;

    /** 区块链类型 */
    @Excel(name = "区块链类型")
    private String chainType;

    /** 币种符号 */
    @Excel(name = "币种符号")
    private String tokenSymbol;

    /** 代币合约地址（主币为空） */
    private String tokenContract;

    /** 交易金额 */
    @Excel(name = "交易金额")
    private BigDecimal amount;

    /** 交易方向 */
    @Excel(name = "交易方向")
    private String direction;

    /** 发送方地址 */
    @Excel(name = "发送方地址")
    private String fromAddress;

    /** 接收方地址 */
    @Excel(name = "接收方地址")
    private String toAddress;

    /** 区块号 */
    @Excel(name = "区块号")
    private Long blockNumber;

    /** 是否确认 */
    @Excel(name = "是否确认")
    private String confirmed;

    /** 确认块数量 */
    @Excel(name = "确认块数量")
    private Long confirmations;

    /** 交易状态 */
    @Excel(name = "交易状态")
    private String txStatus;

    /** 交易时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "交易时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date detectedTime;

    /** 确认完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "确认完成时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date confirmedTime;

    /** 回调状态 */
    @Excel(name = "回调状态")
    private String callbackStatus;

    /** 回调时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "回调时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date callbackTime;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setAddressId(Long addressId) 
    {
        this.addressId = addressId;
    }

    public Long getAddressId() 
    {
        return addressId;
    }

    public void setPlatformId(Long platformId) 
    {
        this.platformId = platformId;
    }

    public Long getPlatformId() 
    {
        return platformId;
    }

    public void setTxHash(String txHash) 
    {
        this.txHash = txHash;
    }

    public String getTxHash() 
    {
        return txHash;
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

    public void setAmount(BigDecimal amount) 
    {
        this.amount = amount;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }

    public void setDirection(String direction) 
    {
        this.direction = direction;
    }

    public String getDirection() 
    {
        return direction;
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

    public void setBlockNumber(Long blockNumber) 
    {
        this.blockNumber = blockNumber;
    }

    public Long getBlockNumber() 
    {
        return blockNumber;
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
            .append("addressId", getAddressId())
            .append("platformId", getPlatformId())
            .append("txHash", getTxHash())
            .append("chainType", getChainType())
            .append("tokenSymbol", getTokenSymbol())
            .append("tokenContract", getTokenContract())
            .append("amount", getAmount())
            .append("direction", getDirection())
            .append("fromAddress", getFromAddress())
            .append("toAddress", getToAddress())
            .append("blockNumber", getBlockNumber())
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
