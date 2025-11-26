package com.ruoyi.blockchain.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 付款地址对象 bc_fee_source_addresses
 * 
 * @author dc
 * @date 2025-11-09
 */
public class BcFeeSourceAddresses extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 平台ID */
    @Excel(name = "平台ID")
    private Long platformId;

    /** 区块链 */
    @Excel(name = "区块链")
    private String chainType;

    /** 出款地址 */
    @Excel(name = "出款地址")
    private String feeAddress;

    /** 私钥(加密) */
    private String privateKeyEncrypted;

    /** 最后使用时间 */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Excel(name = "最后使用时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastUsedTime;

    /** 主币余额 */
    @Excel(name = "主币余额")
    private BigDecimal balance;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

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

    public void setChainType(String chainType) 
    {
        this.chainType = chainType;
    }

    public String getChainType() 
    {
        return chainType;
    }

    public void setFeeAddress(String feeAddress) 
    {
        this.feeAddress = feeAddress;
    }

    public String getFeeAddress() 
    {
        return feeAddress;
    }

    public void setPrivateKeyEncrypted(String privateKeyEncrypted) 
    {
        this.privateKeyEncrypted = privateKeyEncrypted;
    }

    public String getPrivateKeyEncrypted() 
    {
        return privateKeyEncrypted;
    }

    public void setLastUsedTime(Date lastUsedTime) 
    {
        this.lastUsedTime = lastUsedTime;
    }

    public Date getLastUsedTime() 
    {
        return lastUsedTime;
    }

    public void setBalance(BigDecimal balance) 
    {
        this.balance = balance;
    }

    public BigDecimal getBalance() 
    {
        return balance;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("platformId", getPlatformId())
            .append("chainType", getChainType())
            .append("feeAddress", getFeeAddress())
            .append("privateKeyEncrypted", getPrivateKeyEncrypted())
            .append("lastUsedTime", getLastUsedTime())
            .append("balance", getBalance())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
