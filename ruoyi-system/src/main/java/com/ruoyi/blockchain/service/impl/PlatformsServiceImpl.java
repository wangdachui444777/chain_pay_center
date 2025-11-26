package com.ruoyi.blockchain.service.impl;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.security.Md5Utils;
import com.ruoyi.common.utils.uuid.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.blockchain.mapper.PlatformsMapper;
import com.ruoyi.blockchain.domain.Platforms;
import com.ruoyi.blockchain.service.IPlatformsService;
import com.ruoyi.common.core.text.Convert;

/**
 * 下游平台Service业务层处理
 * 
 * @author dc
 * @date 2025-10-27
 */
@Service
public class PlatformsServiceImpl implements IPlatformsService 
{
    @Autowired
    private PlatformsMapper platformsMapper;

    /**
     * 查询下游平台
     * 
     * @param id 下游平台主键
     * @return 下游平台
     */
    @Override
    public Platforms selectPlatformsById(Long id)
    {
        return platformsMapper.selectPlatformsById(id);
    }

    /**
     * 查询下游平台列表
     * 
     * @param platforms 下游平台
     * @return 下游平台
     */
    @Override
    public List<Platforms> selectPlatformsList(Platforms platforms)
    {
        return platformsMapper.selectPlatformsList(platforms);
    }
    /**
     * 获取所有的平台列表，用于下拉查询
     * @return
     */
    public List<Platforms> getPlatformsAll(){
        return platformsMapper.getPlatformsAll();
    }
    /**
     * 新增下游平台
     * 
     * @param platforms 下游平台
     * @return 结果
     */
    @Override
    public int insertPlatforms(Platforms platforms)
    {
        platforms.setCreateTime(DateUtils.getNowDate());
        // 生成唯一 API Key（简短可暴露）
        // 示例格式：PLAT-8位随机字母数字 (预留字段，暂时无用)
        String apiKey = "PLAT-" + RandomStringUtils.randomAlphanumeric(8).toUpperCase();
        platforms.setApiKey(apiKey);

        // 生成 SecretKey（长、不可预测）
        // 使用高强度 SecureRandom + Base64 编码
        String secretKey = generateSecureSecretKey();
        platforms.setSecretKey(secretKey);

        // 默认状态为启用
        if (platforms.getStatus() == null) {
            platforms.setStatus("0");
        }
        return platformsMapper.insertPlatforms(platforms);
    }

    /**
     * 修改下游平台
     * 
     * @param platforms 下游平台
     * @return 结果
     */
    @Override
    public int updatePlatforms(Platforms platforms)
    {
        platforms.setUpdateTime(DateUtils.getNowDate());
        return platformsMapper.updatePlatforms(platforms);
    }

    /**
     * 批量删除下游平台
     * 
     * @param ids 需要删除的下游平台主键
     * @return 结果
     */
    @Override
    public int deletePlatformsByIds(String ids)
    {
        return platformsMapper.deletePlatformsByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除下游平台信息
     * 
     * @param id 下游平台主键
     * @return 结果
     */
    @Override
    public int deletePlatformsById(Long id)
    {
        return platformsMapper.deletePlatformsById(id);
    }

    /**
     * 生成高安全级别的 SecretKey
     */
    private String generateSecureSecretKey() {
        try {
            SecureRandom random = new SecureRandom();
            byte[] bytes = new byte[32]; // 256-bit
            random.nextBytes(bytes);
            String base64 = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

            // 可选增强混淆：拼接UUID或MD5摘要
            String mixed = Md5Utils.hash(UUID.randomUUID() + base64);
            return base64.substring(0, 16) + mixed.substring(0, 16);
        } catch (Exception e) {
            throw new RuntimeException("生成SecretKey失败", e);
        }
    }
}
