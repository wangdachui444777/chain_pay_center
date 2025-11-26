package com.ruoyi.blockchain.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 地址余额（币种级别）对象 bc_address_balances
 * 
 * @author dc
 * @date 2025-10-27
 */
public class AddressBalances extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** ID */
    private Long id;

    /** 关联地址ID */
    @Excel(name = "关联地址ID")
    private Long addressId;

    /** 平台ID */
    @Excel(name = "关联地址ID")
    private Long platformId;


    /** 区块链 */
    @Excel(name = "区块链")
    private String chainType;

    /** 币种符号 */
    @Excel(name = "币种符号")
    private String tokenSymbol;

    /** 合约地址（主币为空） */
    private String tokenContract;

    /** 币种数量 */
    @Excel(name = "币种数量")
    private BigDecimal balance;

    /** 累计充值数量 */
    @Excel(name = "累计充值数量")
    private BigDecimal totalBalance;

    /** USDT价值 */
    @Excel(name = "USDT价值")
    private BigDecimal balanceUsdtValue;


    /** 交易状态 */
    @Excel(name = "交易状态")
    private String status;

    /** 同步时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "同步时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastSyncTime;

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

    public void setBalance(BigDecimal balance) 
    {
        this.balance = balance;
    }

    public BigDecimal getBalance() 
    {
        return balance;
    }

    public void setBalanceUsdtValue(BigDecimal balanceUsdtValue) 
    {
        this.balanceUsdtValue = balanceUsdtValue;
    }

    public BigDecimal getBalanceUsdtValue() 
    {
        return balanceUsdtValue;
    }

    public void setLastSyncTime(Date lastSyncTime) 
    {
        this.lastSyncTime = lastSyncTime;
    }

    public Date getLastSyncTime() 
    {
        return lastSyncTime;
    }
    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(BigDecimal totalBalance) {
        this.totalBalance = totalBalance;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("addressId", getAddressId())
            .append("chainType", getChainType())
            .append("tokenSymbol", getTokenSymbol())
            .append("tokenContract", getTokenContract())
            .append("balance", getBalance())
            .append("balanceUsdtValue", getBalanceUsdtValue())
            .append("lastSyncTime", getLastSyncTime())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }


}
