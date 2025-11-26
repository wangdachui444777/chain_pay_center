package com.ruoyi.blockchain.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 归集配置对象 bc_collection_config
 * 
 * @author dc
 * @date 2025-11-09
 */
public class BcCollectionConfig extends BaseEntity
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

    /** 归集地址(必须充值且需要预留usdt) */
    @Excel(name = "归集地址(必须充值且需要预留usdt)")
    private String targetAddress;

    /** 最小归集阈值 */
    @Excel(name = "最小归集阈值")
    private BigDecimal minCollectAmount;

    /** 自动归集 */
    @Excel(name = "自动归集")
    private Integer enableAutoCollect;

    /** 归集范围 */
    @Excel(name = "归集范围")
    private String tokenScope;

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

    public void setTargetAddress(String targetAddress) 
    {
        this.targetAddress = targetAddress;
    }

    public String getTargetAddress() 
    {
        return targetAddress;
    }

    public void setMinCollectAmount(BigDecimal minCollectAmount) 
    {
        this.minCollectAmount = minCollectAmount;
    }

    public BigDecimal getMinCollectAmount() 
    {
        return minCollectAmount;
    }

    public void setEnableAutoCollect(Integer enableAutoCollect) 
    {
        this.enableAutoCollect = enableAutoCollect;
    }

    public Integer getEnableAutoCollect() 
    {
        return enableAutoCollect;
    }

    public void setTokenScope(String tokenScope) 
    {
        this.tokenScope = tokenScope;
    }

    public String getTokenScope() 
    {
        return tokenScope;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("platformId", getPlatformId())
            .append("chainType", getChainType())
            .append("targetAddress", getTargetAddress())
            .append("minCollectAmount", getMinCollectAmount())
            .append("enableAutoCollect", getEnableAutoCollect())
            .append("tokenScope", getTokenScope())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
