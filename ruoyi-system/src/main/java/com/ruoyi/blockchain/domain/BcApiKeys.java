package com.ruoyi.blockchain.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 区块API配置对象 bc_api_keys
 * 
 * @author dc
 * @date 2025-10-29
 */
public class BcApiKeys extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long id;

    /** 区块链类型 */
    @Excel(name = "区块链类型")
    private String chainType;

    /** API提供商 */
    @Excel(name = "API提供商")
    private String apiProvider;

    /** API密钥 */
    private String apiKey;

    /** API基础URL */
    private String apiUrl;

    /** 每日请求限制 */
    @Excel(name = "每日请求限制")
    private Long dailyLimit;

    /** 今日已用次数 */
    @Excel(name = "今日已用次数")
    private Long usedCount;

    /** 优先级（1-10，数字越小优先级越高） */
    @Excel(name = "优先级", readConverterExp = "1-10，数字越小优先级越高")
    private Long priority;

    /** 状态 */
    @Excel(name = "状态")
    private String status;

    /** 最后使用时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "最后使用时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date lastUsedTime;

    /** 计数重置时间（每日凌晨） */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Excel(name = "计数重置时间", readConverterExp = "每日凌晨")
    private Date resetTime;

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

    public void setApiProvider(String apiProvider) 
    {
        this.apiProvider = apiProvider;
    }

    public String getApiProvider() 
    {
        return apiProvider;
    }

    public void setApiKey(String apiKey) 
    {
        this.apiKey = apiKey;
    }

    public String getApiKey() 
    {
        return apiKey;
    }

    public void setApiUrl(String apiUrl) 
    {
        this.apiUrl = apiUrl;
    }

    public String getApiUrl() 
    {
        return apiUrl;
    }

    public void setDailyLimit(Long dailyLimit) 
    {
        this.dailyLimit = dailyLimit;
    }

    public Long getDailyLimit() 
    {
        return dailyLimit;
    }

    public void setUsedCount(Long usedCount) 
    {
        this.usedCount = usedCount;
    }

    public Long getUsedCount() 
    {
        return usedCount;
    }

    public void setPriority(Long priority) 
    {
        this.priority = priority;
    }

    public Long getPriority() 
    {
        return priority;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setLastUsedTime(Date lastUsedTime) 
    {
        this.lastUsedTime = lastUsedTime;
    }

    public Date getLastUsedTime() 
    {
        return lastUsedTime;
    }

    public void setResetTime(Date resetTime) 
    {
        this.resetTime = resetTime;
    }

    public Date getResetTime() 
    {
        return resetTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("chainType", getChainType())
            .append("apiProvider", getApiProvider())
            .append("apiKey", getApiKey())
            .append("apiUrl", getApiUrl())
            .append("dailyLimit", getDailyLimit())
            .append("usedCount", getUsedCount())
            .append("priority", getPriority())
            .append("status", getStatus())
            .append("lastUsedTime", getLastUsedTime())
            .append("resetTime", getResetTime())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
