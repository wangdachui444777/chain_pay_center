package com.ruoyi.blockchain.domain;

import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用户地址对象 bc_user_addresses
 * 
 * @author dc
 * @date 2025-10-27
 */
public class UserAddresses extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 地址ID */
    private Long id;

    /** 所属平台ID */
    @Excel(name = "所属平台ID")
    private Long platformId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private String userId;

    /** 区块链 */
    @Excel(name = "区块链")
    private String chainType;

    /** 区块链地址 */
    @Excel(name = "区块链地址")
    private String address;

    /** 私钥 */
    private String privateKey;

    /** 最近同步时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "最近同步时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastSyncTime;

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

    public void setUserId(String userId) 
    {
        this.userId = userId;
    }

    public String getUserId() 
    {
        return userId;
    }

    public void setChainType(String chainType) 
    {
        this.chainType = chainType;
    }

    public String getChainType() 
    {
        return chainType;
    }

    public void setAddress(String address) 
    {
        this.address = address;
    }

    public String getAddress() 
    {
        return address;
    }

    public void setPrivateKey(String privateKey) 
    {
        this.privateKey = privateKey;
    }

    public String getPrivateKey() 
    {
        return privateKey;
    }

    public void setLastSyncTime(Date lastSyncTime) 
    {
        this.lastSyncTime = lastSyncTime;
    }

    public Date getLastSyncTime() 
    {
        return lastSyncTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("platformId", getPlatformId())
            .append("userId", getUserId())
            .append("chainType", getChainType())
            .append("address", getAddress())
            .append("privateKey", getPrivateKey())
            .append("lastSyncTime", getLastSyncTime())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
