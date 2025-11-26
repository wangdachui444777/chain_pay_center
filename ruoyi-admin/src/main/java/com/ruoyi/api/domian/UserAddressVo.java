package com.ruoyi.api.domian;

import io.swagger.annotations.ApiModelProperty;


public class UserAddressVo {
    /** 区块链 */
    @ApiModelProperty(name = "区块链")
    private String chainType;

    /** 区块链地址 */
    @ApiModelProperty(name = "区块链地址")
    private String address;

    public String getChainType() {
        return chainType;
    }

    public void setChainType(String chainType) {
        this.chainType = chainType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
