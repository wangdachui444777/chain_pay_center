package com.ruoyi.quartz.task;

import com.ruoyi.bc.domain.BlockchainAddress;
import com.ruoyi.bc.service.AddressGeneratorFactory;
import com.ruoyi.blockchain.domain.AddressPool;
import com.ruoyi.blockchain.service.IAddressPoolService;
import com.ruoyi.common.core.domain.entity.SysDictData;
import com.ruoyi.common.utils.DictUtils;
import com.ruoyi.common.utils.EncryptUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 定时生成区块链地址的任务
 *
 * 功能说明：
 *  - 从字典表（bc_chain_type）中获取支持的链（TRON / ETH等）
 *  - 为每个链生成地址对（地址 + 私钥）
 *  - 保存到临时表（address_pool）
 *  - 当某条链的临时表数量不足 num 时自动补充
 *
 * 表设计要求：
 *  - address_pool 用于缓存未分配的区块链地址
 */
@Component("createAddressTask")
public class CreateAddressTask {

    private static final Logger log = LoggerFactory.getLogger(CreateAddressTask.class);

    private IAddressPoolService addressPoolService;

    @Autowired
    public void setAddressPoolService(IAddressPoolService addressPoolService) {
        this.addressPoolService = addressPoolService;
    }

    /**
     * 生成指定数量的地址（按区块链类型分别检查数量，不足则自动补齐）
     * @param num 每种链最少保持的地址数
     */
    public void create(Integer num) {
        log.info("开始执行定时任务：生成区块链地址，每种链最少保留 {} 条", num);

        // 1获取字典配置：区块链类型（bc_chain_type）
        List<SysDictData> chainDictList = DictUtils.getDictCache("bc_chain_type");
        if (chainDictList == null || chainDictList.isEmpty()) {
            log.warn("未找到字典类型 bc_chain_type，请先在系统字典中配置区块链类型");
            return;
        }

        // 遍历所有启用状态的链
        for (SysDictData dictData : chainDictList) {
            if ("1".equals(dictData.getStatus())) {
                // 状态为禁用（1），跳过
                continue;
            }
            String chainType = dictData.getDictValue();
            // 查询当前链的临时表数量
            int existingCount = addressPoolService.countByChainType(chainType);
            int toCreate = num - existingCount;

            if (toCreate <= 0) {
                log.info("链 [{}] 地址数量已达标 ({} >= {})，跳过生成", chainType, existingCount, num);
                continue;
            }

            log.info("链 [{}] 当前数量 {}，需补充 {} 条地址", chainType, existingCount, toCreate);

            //批量生成地址
            List<AddressPool> newAddressList = new ArrayList<>();
            for (int i = 0; i < toCreate; i++) {
                try {
                    // 每批次可以使用相同助记词，也可以每次生成新的（按业务需要）
                    String mnemonic = AddressGeneratorFactory.getMnemonic();
                    BlockchainAddress addressInfo = AddressGeneratorFactory.generateFromMnemonic(chainType, mnemonic, 0);

                    String address = addressInfo.getAddress();
                    String privateKey = addressInfo.getPrivateKeyHex();
                    AddressPool pool = new AddressPool();
                    pool.setChainType(chainType);
                    pool.setAddress(address);
                    // 保存数据库时加密私钥（用地址做盐值）
                    pool.setPrivateKey(EncryptUtils.desEncryption_new(privateKey, address));
                    newAddressList.add(pool);
                } catch (Exception e) {
                    log.error("生成链 [{}] 地址失败：{}", chainType, e.getMessage(), e);
                }
            }

            // 批量入库
            if (!newAddressList.isEmpty()) {
                int inserted = addressPoolService.batchInsertAddressPool(newAddressList);
                log.info("链 [{}] 成功生成 {} 条地址并保存到临时表", chainType, inserted);
            }
        }

        log.info("区块链地址生成任务执行完毕");
    }
}
