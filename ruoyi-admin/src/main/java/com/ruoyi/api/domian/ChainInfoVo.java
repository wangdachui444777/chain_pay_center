package com.ruoyi.api.domian;

import com.ruoyi.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

public class ChainInfoVo {

    /** 记录ID */
    private Long id;

    /** 区块链类型 */
    @ApiModelProperty(name = "区块链类型")
    private String chainType;

    /** 币种符号 */
    @ApiModelProperty(name = "币种符号")
    private String tokenSymbol;

    /** 代币合约地址（主币为空） */
    private String tokenContract;

    /** 代币精度 */
    @ApiModelProperty(name = "代币精度")
    private Integer decimals;

    /** USDT汇率 */
    @ApiModelProperty(name = "USDT汇率")
    private BigDecimal priceInUsdt;

    /** 是否主币 */
    @ApiModelProperty(name = "是否主币 1主币，")
    private Integer isMainCoin;


    /** 代币类型 */
    @ApiModelProperty(name = "代币类型")
    private String tokenType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getChainType() {
        return chainType;
    }

    public void setChainType(String chainType) {
        this.chainType = chainType;
    }

    public String getTokenSymbol() {
        return tokenSymbol;
    }

    public void setTokenSymbol(String tokenSymbol) {
        this.tokenSymbol = tokenSymbol;
    }

    public String getTokenContract() {
        return tokenContract;
    }

    public void setTokenContract(String tokenContract) {
        this.tokenContract = tokenContract;
    }

    public Integer getDecimals() {
        return decimals;
    }

    public void setDecimals(Integer decimals) {
        this.decimals = decimals;
    }

    public BigDecimal getPriceInUsdt() {
        return priceInUsdt;
    }

    public void setPriceInUsdt(BigDecimal priceInUsdt) {
        this.priceInUsdt = priceInUsdt;
    }

    public Integer getIsMainCoin() {
        return isMainCoin;
    }

    public void setIsMainCoin(Integer isMainCoin) {
        this.isMainCoin = isMainCoin;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
