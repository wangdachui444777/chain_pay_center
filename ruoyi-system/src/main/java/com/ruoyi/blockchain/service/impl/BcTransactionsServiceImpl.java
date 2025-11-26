package com.ruoyi.blockchain.service.impl;

import java.util.Date;
import java.util.List;

import com.ruoyi.blockchain.domain.*;
import com.ruoyi.blockchain.mapper.AddressBalancesMapper;
import com.ruoyi.blockchain.service.IAddressBalancesService;
import com.ruoyi.blockchain.service.IBcFeeSourceAddressesService;
import com.ruoyi.blockchain.service.IUserAddressesService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.blockchain.mapper.BcTransactionsMapper;
import com.ruoyi.blockchain.service.IBcTransactionsService;
import com.ruoyi.common.core.text.Convert;

/**
 * 地址充值记录Service业务层处理
 * 
 * @author dc
 * @date 2025-10-30
 */
@Service
public class BcTransactionsServiceImpl implements IBcTransactionsService 
{
    @Autowired
    private BcTransactionsMapper bcTransactionsMapper;
    @Autowired
    private IAddressBalancesService addressBalancesService;

    @Autowired
    private IUserAddressesService userAddressesService;
    @Autowired
    private IBcFeeSourceAddressesService sourceAddressesService;

    /**
     * 查询地址充值记录
     * 
     * @param id 地址充值记录主键
     * @return 地址充值记录
     */
    @Override
    public BcTransactions selectBcTransactionsById(Long id)
    {
        return bcTransactionsMapper.selectBcTransactionsById(id);
    }

    /**
     * 查询地址充值记录
     *
     * @param txHash 交易hash
     * @return 地址充值记录
     */
    @Override
    public BcTransactions selectBcTransactionsByHash(String txHash)
    {
        return bcTransactionsMapper.selectBcTransactionByHash(txHash);
    }
    /**
     * 查询地址充值记录列表
     * 
     * @param bcTransactions 地址充值记录
     * @return 地址充值记录
     */
    @Override
    public List<BcTransactions> selectBcTransactionsList(BcTransactions bcTransactions)
    {
        return bcTransactionsMapper.selectBcTransactionsList(bcTransactions);
    }

    /**
     * 新增地址充值记录
     * 
     * @param bcTransactions 地址充值记录
     * @return 结果
     */
    @Override
    public int insertBcTransactions(BcTransactions bcTransactions)
    {
        bcTransactions.setCreateTime(DateUtils.getNowDate());
        return bcTransactionsMapper.insertBcTransactions(bcTransactions);
    }
    public boolean existsByTxHash(String txHash){

      BcTransactions transactions=  this.selectBcTransactionsByHash(txHash);
      if(transactions!=null){
          return true;
      }
      return false;
    }
    /**
     * 处理监控来的地址充值记录
     *
     * @param tx 地址充值记录
     * @return 结果
     */
    public boolean saveBcTransactions(String chainType, Long addressId, ApiBcTransaction tx){

        //  检查交易是否已入库
        boolean exists = this.existsByTxHash(tx.getTxHash());
        if (exists) {
            return false;
        }
        //根据地址id查询平台
       UserAddresses userAddresses= userAddressesService.selectUserAddressesById(addressId);
       if (userAddresses==null){
           return false;
       }
        BcTransactions transaction = new BcTransactions();
        transaction.setAddressId(addressId);
        transaction.setPlatformId(userAddresses.getPlatformId()); // 需要查询 bc_user_addresses 表
        transaction.setTxHash(tx.getTxHash());
        transaction.setChainType(chainType);
        transaction.setTokenSymbol(tx.getTokenSymbol());
        transaction.setTokenContract(tx.getTokenContract());
        transaction.setAmount(tx.getAmount());
        transaction.setDirection("1"); // 充值
        transaction.setFromAddress(tx.getFromAddress());
        transaction.setToAddress(tx.getToAddress());
        transaction.setBlockNumber(tx.getBlockNumber());
        transaction.setTxStatus(tx.getStatus());
        transaction.setConfirmations(0L);
        transaction.setDetectedTime(DateUtils.getNowDate());
        transaction.setConfirmed("0"); // 未确认

        /**
         * 内部转账的-主要是手续费
         */
        BcFeeSourceAddresses sourceAddresses=sourceAddressesService.getPayAddressCache(userAddresses.getPlatformId(),chainType,tx.getFromAddress());
        if (sourceAddresses!=null){
            return true; //内部转账不记录
            /**transaction.setFromAddress(tx.getFromAddress()+"-内");
            transaction.setConfirmed("1");
            transaction.setCallbackStatus("2");
            transaction.setCallbackTime(DateUtils.getNowDate());
            transaction.setTxStatus("2");**/
        }

        if ("3".equals(tx.getStatus())){
            transaction.setConfirmed("1"); // 已确认
            transaction.setConfirmedTime(DateUtils.getNowDate());
        }else{
            if (StringUtils.isNotBlank(tx.getConfirmed()) && "1".equals(tx.getConfirmed())){
                transaction.setConfirmedTime(DateUtils.getNowDate());
                transaction.setConfirmations(19L);
                //如果是确认的，更新余额
                addressBalancesService.updateOrSaveBalances(addressId,
                        chainType,
                        tx.getTokenSymbol(),
                        tx.getAmount(),
                        tx.getTokenContract(),
                        userAddresses.getPlatformId());
            }
        }
        if(tx.getBlockTimestamp()!=null){
            transaction.setDetectedTime(new Date(tx.getBlockTimestamp()));
        }
        transaction.setCallbackStatus("1"); // 待回调
        return this.insertBcTransactions(transaction)>0;

    }

    /**
     * 修改地址充值记录
     * 
     * @param bcTransactions 地址充值记录
     * @return 结果
     */
    @Override
    public int updateBcTransactions(BcTransactions bcTransactions)
    {
        bcTransactions.setUpdateTime(DateUtils.getNowDate());
        return bcTransactionsMapper.updateBcTransactions(bcTransactions);
    }

    /**
     * 批量删除地址充值记录
     * 
     * @param ids 需要删除的地址充值记录主键
     * @return 结果
     */
    @Override
    public int deleteBcTransactionsByIds(String ids)
    {
        return bcTransactionsMapper.deleteBcTransactionsByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除地址充值记录信息
     * 
     * @param id 地址充值记录主键
     * @return 结果
     */
    @Override
    public int deleteBcTransactionsById(Long id)
    {
        return bcTransactionsMapper.deleteBcTransactionsById(id);
    }
}
