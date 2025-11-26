package com.ruoyi.blockchain.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 下游平台对象 bc_platforms
 * 
 * @author dc
 * @date 2025-10-27
 */
public class Platforms extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 平台ID */
    private Long id;

    /** 平台名称 */
    @Excel(name = "平台名称")
    private String platformName;

    /** 签名SecretKey */
    @Excel(name = "签名SecretKey")
    private String secretKey;

    /** apiKey */
    private String apiKey;

    /** 回调URL */
    private String callbackUrl;

    /**
     * 提现url
     */
    private String withdrawUrl;

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

    public void setPlatformName(String platformName) 
    {
        this.platformName = platformName;
    }

    public String getPlatformName() 
    {
        return platformName;
    }

    public void setSecretKey(String secretKey) 
    {
        this.secretKey = secretKey;
    }

    public String getSecretKey() 
    {
        return secretKey;
    }

    public void setApiKey(String apiKey) 
    {
        this.apiKey = apiKey;
    }

    public String getApiKey() 
    {
        return apiKey;
    }

    public void setCallbackUrl(String callbackUrl) 
    {
        this.callbackUrl = callbackUrl;
    }

    public String getCallbackUrl() 
    {
        return callbackUrl;
    }

    public String getWithdrawUrl() {
        return withdrawUrl;
    }

    public void setWithdrawUrl(String withdrawUrl) {
        this.withdrawUrl = withdrawUrl;
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
            .append("platformName", getPlatformName())
            .append("secretKey", getSecretKey())
            .append("apiKey", getApiKey())
            .append("callbackUrl", getCallbackUrl())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
