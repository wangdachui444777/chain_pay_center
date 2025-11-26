package com.ruoyi.api.domian;

import com.ruoyi.common.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class AppleWithdrawReq extends BaseEntityReq {

    /** 下游单号 */
    @ApiModelProperty(name = "下游单号")
    @NotNull(message = "requestNo不能为空")
    @Size(min = 2, max = 64, message = "requestNo长度错误")
    private String requestNo;

    /** 区块链 */
    @ApiModelProperty(name = "区块链")
    @NotNull(message = "chainType不能为空")
    @Size(min = 2, max = 20, message = "chainType长度错误")
    private String chainType;

    /** 币种符号 */
    @ApiModelProperty(name = "币种符号")
    @NotNull(message = "tokenSymbol不能为空")
    @Size(min = 2, max = 20, message = "tokenSymbol长度错误")
    private String tokenSymbol;

    /** 收款地址 */
    @ApiModelProperty(name = "收款地址")
    @NotNull(message = "toAddress不能为空")
    @Size(min = 10, max = 128, message = "toAddress长度错误")
    private String toAddress;

    /** 提现金额 */
    @ApiModelProperty(name = "提现金额")
    private BigDecimal amount;



    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
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


    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
