package com.ruoyi.blockchain.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 币种配置对象 bc_token_prices
 * 
 * @author dc
 * @date 2025-10-29
 */
public class TokenPrices extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long id;

    /** 区块链类型 */
    @Excel(name = "区块链类型")
    private String chainType;

    /** 币种符号 */
    @Excel(name = "币种符号")
    private String tokenSymbol;

    /** 代币合约地址（主币为空） */
    private String tokenContract;

    /** 代币精度 */
    @Excel(name = "代币精度")
    private Integer decimals;

    /** USDT汇率 */
    @Excel(name = "USDT汇率")
    private BigDecimal priceInUsdt;

    /** 是否主币 */
    @Excel(name = "是否主币")
    private Integer isMainCoin;

    /** 是否启用监听（0禁用 1启用） */
    @Excel(name = "是否启用监听", readConverterExp = "0=禁用,1=启用")
    private Integer enabled;

    /** 代币类型 */
    @Excel(name = "代币类型")
    private String tokenType;

    /** 最后更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "最后更新时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastUpdate;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
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

    public void setDecimals(Integer decimals)
    {
        this.decimals = decimals;
    }

    public Integer getDecimals()
    {
        return decimals;
    }

    public void setPriceInUsdt(BigDecimal priceInUsdt) 
    {
        this.priceInUsdt = priceInUsdt;
    }

    public BigDecimal getPriceInUsdt() 
    {
        return priceInUsdt;
    }

    public void setIsMainCoin(Integer isMainCoin) 
    {
        this.isMainCoin = isMainCoin;
    }

    public Integer getIsMainCoin() 
    {
        return isMainCoin;
    }

    public void setEnabled(Integer enabled) 
    {
        this.enabled = enabled;
    }

    public Integer getEnabled() 
    {
        return enabled;
    }

    public void setTokenType(String tokenType) 
    {
        this.tokenType = tokenType;
    }

    public String getTokenType() 
    {
        return tokenType;
    }

    public void setLastUpdate(Date lastUpdate) 
    {
        this.lastUpdate = lastUpdate;
    }

    public Date getLastUpdate() 
    {
        return lastUpdate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("chainType", getChainType())
            .append("tokenSymbol", getTokenSymbol())
            .append("tokenContract", getTokenContract())
            .append("decimals", getDecimals())
            .append("priceInUsdt", getPriceInUsdt())
            .append("isMainCoin", getIsMainCoin())
            .append("enabled", getEnabled())
            .append("tokenType", getTokenType())
            .append("lastUpdate", getLastUpdate())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
