package com.ruoyi.blockchain.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 临时地址对象 bc_address_pool
 * 
 * @author dc
 * @date 2025-10-28
 */
public class AddressPool extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 编号ID */
    @Excel(name = "编号ID")
    private Long id;

    /** 区块链 */
    @Excel(name = "区块链")
    private String chainType;

    /** 区块链地址 */
    @Excel(name = "区块链地址")
    private String address;

    /** 私钥(加密后) */
    private String privateKey;

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

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("chainType", getChainType())
            .append("address", getAddress())
            .append("privateKey", getPrivateKey())
            .toString();
    }
}
