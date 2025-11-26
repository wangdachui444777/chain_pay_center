package com.ruoyi.bc.domain;


import java.math.BigDecimal;
import java.util.Date;

/**
 * 代币价格数据模型
 */
public class TokenPriceData {

    /** 币种符号 */
    private String tokenSymbol;

    /** USDT 价格 */
    private BigDecimal priceInUsdt;

    /** 价格源 */
    private String source;

    /** 获取时间 */
    private Date fetchTime;

    /** 是否成功 */
    private Boolean success;

    /** 错误信息 */
    private String errorMessage;

    public String getTokenSymbol() {
        return tokenSymbol;
    }

    public void setTokenSymbol(String tokenSymbol) {
        this.tokenSymbol = tokenSymbol;
    }

    public BigDecimal getPriceInUsdt() {
        return priceInUsdt;
    }

    public void setPriceInUsdt(BigDecimal priceInUsdt) {
        this.priceInUsdt = priceInUsdt;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Date getFetchTime() {
        return fetchTime;
    }

    public void setFetchTime(Date fetchTime) {
        this.fetchTime = fetchTime;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public TokenPriceData(String tokenSymbol) {
        this.tokenSymbol = tokenSymbol;
        this.fetchTime = new Date();
        this.success = false;
    }

    public static TokenPriceData success(String tokenSymbol, BigDecimal price, String source) {
        TokenPriceData data = new TokenPriceData(tokenSymbol);
        data.setPriceInUsdt(price);
        data.setSource(source);
        data.setSuccess(true);
        return data;
    }

    public static TokenPriceData failure(String tokenSymbol, String errorMessage) {
        TokenPriceData data = new TokenPriceData(tokenSymbol);
        data.setErrorMessage(errorMessage);
        data.setSuccess(false);
        return data;
    }
}