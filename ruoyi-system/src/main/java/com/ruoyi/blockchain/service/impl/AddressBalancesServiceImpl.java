package com.ruoyi.blockchain.service.impl;

import java.math.BigDecimal;
import java.util.*;

import com.ruoyi.blockchain.domain.*;
import com.ruoyi.blockchain.service.ITokenPricesService;
import com.ruoyi.blockchain.service.IUserAddressesService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.blockchain.mapper.AddressBalancesMapper;
import com.ruoyi.blockchain.service.IAddressBalancesService;
import com.ruoyi.common.core.text.Convert;

/**
 * 地址余额（币种级别）Service业务层处理
 * 
 * @author dc
 * @date 2025-10-27
 */
@Service
public class AddressBalancesServiceImpl implements IAddressBalancesService 
{
    @Autowired
    private AddressBalancesMapper addressBalancesMapper;
    @Autowired
    private ITokenPricesService iTokenPricesService;

    @Autowired
    private IUserAddressesService userAddressesService;

    /**
     * 查询地址余额（币种级别）
     * 
     * @param id 地址余额（币种级别）主键
     * @return 地址余额（币种级别）
     */
    @Override
    public AddressBalances selectAddressBalancesById(Long id)
    {
        return addressBalancesMapper.selectAddressBalancesById(id);
    }

    /**
     * 查询地址余额（币种级别）列表
     * 
     * @param addressBalances 地址余额（币种级别）
     * @return 地址余额（币种级别）
     */
    @Override
    public List<AddressBalances> selectAddressBalancesList(AddressBalances addressBalances)
    {
        return addressBalancesMapper.selectAddressBalancesList(addressBalances);
    }

    /**
     *
     * @param chainType
     * @param platformId
     * @param lastId
     * @param batchSize
     * @return
     */
    public List<AddressBalances> selectBalancesListAfterId(String chainType,Long platformId,Long lastId, int batchSize){
        return addressBalancesMapper.selectBalancesListAfterId(chainType,platformId,lastId,batchSize);
    }

    /**
     * 获取要归集的数据
     * @param collectionConfig 归集配置
     * @param balancesList 用户地址
     * @return
     */
    public  List<BcConsolidationDetail> getCollerctionAddrList(BcCollectionConfig collectionConfig,List<AddressBalances> balancesList, String batchNo){
        List<BcConsolidationDetail> detailList=new ArrayList<>();
        //是否开启自动归集（1=是，0=否
        if (collectionConfig==null || 0==collectionConfig.getEnableAutoCollect()){
           return detailList;
        }
        if (balancesList == null || balancesList.isEmpty()) {
            return detailList; // 没有更多数据
        }
        // 最小归集阈值（达到该金额才归集）
        BigDecimal minAmount= collectionConfig.getMinCollectAmount();
        //归集范围：1=全部代币，2=主币，3=仅USDT
        String tokenScope=collectionConfig.getTokenScope();
        String toAddr=collectionConfig.getTargetAddress();

        for (AddressBalances ab:balancesList) {
            String tokenSymbol=ab.getTokenSymbol();
            BigDecimal usdtAmount= ab.getBalanceUsdtValue();
            if (ab.getBalance().compareTo(BigDecimal.ZERO)<1){
                continue;
            }
            if (!shouldCollect(tokenScope, tokenSymbol)) {
                continue; // 不归集，跳过
            }
            if (usdtAmount.compareTo(minAmount)<0){
                continue; // 不归集，跳过
            }
            UserAddresses userAddresses=userAddressesService.selectUserAddressesById(ab.getAddressId());
            // 需要记录在 归集表中
            BcConsolidationDetail consolidationDetail=new BcConsolidationDetail();
            consolidationDetail.setBatchNo(batchNo);
            consolidationDetail.setPlatformId(ab.getPlatformId());
            consolidationDetail.setChainType(ab.getChainType());
            consolidationDetail.setAddressId(userAddresses.getId());
            consolidationDetail.setAddressBalanceId(ab.getId());
            consolidationDetail.setTokenSymbol(tokenSymbol);
            //余额先不管，等归集的时候再查一次，防止遗漏
            consolidationDetail.setAmount(BigDecimal.ZERO);
            consolidationDetail.setFromAddress(userAddresses.getAddress());
            consolidationDetail.setToAddress(toAddr);
            consolidationDetail.setTokenContract(ab.getTokenContract());
            consolidationDetail.setStartTime(DateUtils.getNowDate());
            consolidationDetail.setCreateTime(DateUtils.getNowDate());
            detailList.add(consolidationDetail);
            //修改为归集中。
            this.updateAddressStatus(ab.getId(),"1");


        }
        return detailList;
    }
    private static final Set<String> MAIN_COINS = new HashSet<>(Arrays.asList(
            "TRX", "ETH","BNB" // 这里后面想加主币就继续加，比如 "BNB", "MATIC", ...
    ));
    /**
     * 判断是否需要归集
     * @param tokenScope 1=全部代币 2=主币 3=只是usdt
     * @param tokenSymbol 币种符号
     */
    public  boolean shouldCollect(String tokenScope, String tokenSymbol) {
        if (tokenSymbol == null) return false;
        tokenSymbol = tokenSymbol.toUpperCase();
        switch (tokenScope) {
            case "1":
                return !MAIN_COINS.contains(tokenSymbol); // 全部代币

            case "2":
                return MAIN_COINS.contains(tokenSymbol); // 归集主币，其它跳过

            case "3":
                return "USDT".equals(tokenSymbol); // 只归集 USDT
            default:
                return false; // 无效配置不处理
        }
    }

    /**
     * 新增地址余额（币种级别）
     * 
     * @param addressBalances 地址余额（币种级别）
     * @return 结果
     */
    @Override
    public int insertAddressBalances(AddressBalances addressBalances)
    {
        addressBalances.setCreateTime(DateUtils.getNowDate());
        return addressBalancesMapper.insertAddressBalances(addressBalances);
    }

    /**
     * 修改地址余额（币种级别）
     * 
     * @param addressBalances 地址余额（币种级别）
     * @return 结果
     */
    @Override
    public int updateAddressBalances(AddressBalances addressBalances)
    {
        addressBalances.setUpdateTime(DateUtils.getNowDate());
        return addressBalancesMapper.updateAddressBalances(addressBalances);
    }

    /**
     * 根据addressId修改信息（多个）
     * @param addressBalances
     * @return
     */
    public int updateAddressBalancesByAddressId(AddressBalances addressBalances)
    {
        addressBalances.setUpdateTime(DateUtils.getNowDate());
        return addressBalancesMapper.updateAddressBalancesByAddressId(addressBalances);
    }
    /**
     * 修改状态
     * @param id
     * @param status
     * @return
     */
    public int updateAddressStatus(Long id,String status){
        AddressBalances updateData= BeanUtils.instantiateClass(AddressBalances.class);
        updateData.setStatus(status);
        updateData.setId(id);
        return this.updateAddressBalances(updateData);
    }

    /**
     * 根据地址ID修改状态(多个)
     * @param addressId
     * @param status
     * @return
     */
    public int updateStatusByAddressId(Long addressId,String status){
        AddressBalances updateData= BeanUtils.instantiateClass(AddressBalances.class);
        updateData.setStatus(status);
        updateData.setAddressId(addressId);
        return updateAddressBalancesByAddressId(updateData);
    }

    /**
     * 新增或者更新余额
     * 顺便更新usdt价格
     * @param addressId
     * @param ChainType
     * @param tokenSymbol
     * @param amount
     * @return
     */
    public boolean updateOrSaveBalances(Long addressId, String ChainType, String tokenSymbol, BigDecimal amount,String tokenContract,Long pId){

        if (amount.compareTo(BigDecimal.ZERO)<1){
            return false;
        }
        //先判断是否存在
        AddressBalances query=new AddressBalances();
        query.setAddressId(addressId);
        query.setTokenSymbol(tokenSymbol);
        List<AddressBalances> tmpList= this.selectAddressBalancesList(query);

        //获取usdt价格
       TokenPrices prices= iTokenPricesService.getTokenCacheByChain(ChainType,tokenSymbol);
       BigDecimal usdtVal=BigDecimal.ONE;
        if (usdtVal.compareTo(BigDecimal.ZERO)>0){
            usdtVal=prices.getPriceInUsdt();
        }
       if(tmpList.size()>0){// 需要更新
           AddressBalances tmpBalances=tmpList.get(0);
           tmpBalances.setLastSyncTime(DateUtils.getNowDate());
           BigDecimal allAmount=tmpBalances.getBalance().add(amount);
           BigDecimal totalAmount=tmpBalances.getTotalBalance().add(amount);
           tmpBalances.setBalance(allAmount);
           tmpBalances.setTotalBalance(totalAmount);
           tmpBalances.setBalanceUsdtValue(allAmount.multiply(usdtVal));
           //归集完成后 修改成0 ，
           //tmpBalances.setStatus("0");
          return this.updateAddressBalances(tmpBalances)>0;

       }else{
           //新增
           AddressBalances addData=new AddressBalances();
           addData.setChainType(ChainType);
           addData.setTokenSymbol(tokenSymbol);
           addData.setPlatformId(pId);
           addData.setAddressId(addressId);
           addData.setBalance(amount);
           addData.setTotalBalance(amount);
           addData.setTokenContract(tokenContract);
           addData.setLastSyncTime(DateUtils.getNowDate());
           addData.setBalanceUsdtValue(amount.multiply(usdtVal));
          return this.insertAddressBalances(addData)>0;

       }

    }

    /**
     * 批量删除地址余额（币种级别）
     * 
     * @param ids 需要删除的地址余额（币种级别）主键
     * @return 结果
     */
    @Override
    public int deleteAddressBalancesByIds(String ids)
    {
        return 1;//addressBalancesMapper.deleteAddressBalancesByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除地址余额（币种级别）信息
     * 
     * @param id 地址余额（币种级别）主键
     * @return 结果
     */
    @Override
    public int deleteAddressBalancesById(Long id)
    {

        return 1;//addressBalancesMapper.deleteAddressBalancesById(id);
    }
}
