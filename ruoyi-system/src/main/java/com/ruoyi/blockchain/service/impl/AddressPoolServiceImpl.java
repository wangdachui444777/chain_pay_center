package com.ruoyi.blockchain.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.blockchain.mapper.AddressPoolMapper;
import com.ruoyi.blockchain.domain.AddressPool;
import com.ruoyi.blockchain.service.IAddressPoolService;
import com.ruoyi.common.core.text.Convert;

/**
 * 临时地址Service业务层处理
 * 
 * @author dc
 * @date 2025-10-28
 */
@Service
public class AddressPoolServiceImpl implements IAddressPoolService 
{
    @Autowired
    private AddressPoolMapper addressPoolMapper;

    /**
     * 查询临时地址
     * 
     * @param id 临时地址主键
     * @return 临时地址
     */
    @Override
    public AddressPool selectAddressPoolById(Long id)
    {
        return addressPoolMapper.selectAddressPoolById(id);
    }

    /**
     * 查询临时地址列表
     * 
     * @param addressPool 临时地址
     * @return 临时地址
     */
    @Override
    public List<AddressPool> selectAddressPoolList(AddressPool addressPool)
    {
        return addressPoolMapper.selectAddressPoolList(addressPool);
    }
    /**
     * 根据链类型批量查询
     */
    @Override
    public List<AddressPool> selectByChainTypes(String chainType) {
        List<String> chainTypes=new ArrayList<>();
        List<AddressPool> addressPoolList=new ArrayList<>();
        // 如果链为空，从字典中取全部
        if (StringUtils.isEmpty(chainType)) {
            List<SysDictData> dicts = DictUtils.getDictCache("bc_chain_type");
            chainTypes = dicts.stream()
                    .filter(d -> !"1".equals(d.getStatus()))  // 跳过禁用
                    .map(SysDictData::getDictValue)
                    .collect(Collectors.toList());
        }else{
            chainTypes.add(chainType);
        }

        // 循环每种链类型单独查询并删除
        for (String t : chainTypes) {
            AddressPool addr = addressPoolMapper.selectByChainTypes(t);
            if (addr != null) {
                addressPoolMapper.deleteAddressPoolById (addr.getId()); // 删除已取出的记录
                addressPoolList.add(addr);
            }
        }
        return addressPoolList;
    }
    /**
     * 根据链类型统计未分配地址数量
     */
    @Override
    public int countByChainType(String chainType) {
        return addressPoolMapper.countByChainType(chainType);
    }
    /**
     * 批量插入地址池
     */
    @Override
    public int batchInsertAddressPool(List<AddressPool> addressList) {
        return addressPoolMapper.batchInsertAddressPool(addressList);
    }

    /**
     * 新增临时地址
     * 
     * @param addressPool 临时地址
     * @return 结果
     */
    @Override
    public int insertAddressPool(AddressPool addressPool)
    {
        return addressPoolMapper.insertAddressPool(addressPool);
    }

    /**
     * 修改临时地址
     * 
     * @param addressPool 临时地址
     * @return 结果
     */
    @Override
    public int updateAddressPool(AddressPool addressPool)
    {
        return addressPoolMapper.updateAddressPool(addressPool);
    }

    /**
     * 批量删除临时地址
     * 
     * @param ids 需要删除的临时地址主键
     * @return 结果
     */
    @Override
    public int deleteAddressPoolByIds(String ids)
    {
        return addressPoolMapper.deleteAddressPoolByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除临时地址信息
     * 
     * @param id 临时地址主键
     * @return 结果
     */
    @Override
    public int deleteAddressPoolById(Long id)
    {
        return addressPoolMapper.deleteAddressPoolById(id);
    }
}
