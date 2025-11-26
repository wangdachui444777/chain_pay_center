package com.ruoyi.api.domian;

import io.swagger.annotations.ApiModelProperty;


public class UserAddressReq extends BaseEntityReq {
    @ApiModelProperty("区块链，TRX，ETH")
    private String  chainType;

    @ApiModelProperty("下游用户的标识")
    private String uuid;

    public String getChainType() {
        return chainType;
    }

    public void setChainType(String chainType) {
        this.chainType = chainType;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
