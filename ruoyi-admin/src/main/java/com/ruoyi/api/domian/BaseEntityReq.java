package com.ruoyi.api.domian;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class BaseEntityReq {
    @ApiModelProperty("平台ID")
    @NotNull(message = "平台ID不能为空")
    private Long platformId;
    @ApiModelProperty("10位时间戳")
    @NotNull(message = "时间戳不能为空")
    private Long timestamp;
    @ApiModelProperty("签名-md5大写")
    @NotBlank(message = "签名不能为空")
    @Size(min = 32, max = 32, message = "签名不正确")
    private String sign;

    public Long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(Long platformId) {
        this.platformId = platformId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
