package com.ruoyi.bc.config;
import com.ruoyi.blockchain.domain.BcApiKeys;
import com.ruoyi.blockchain.service.IBcApiKeysService;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class Web3jManager {

    private final IBcApiKeysService apiKeyService;

    // 缓存 Web3j 实例，避免重复创建连接
    private final Map<String, Web3j> web3jCache = new ConcurrentHashMap<>();

    private final Map<Web3j, Long> web3jToKeyId = new ConcurrentHashMap<>();

    public Web3jManager(IBcApiKeysService apiKeyService) {
        this.apiKeyService = apiKeyService;
    }

    /**
     * 获取一个可用的 Web3j 实例（自动轮询多个 RPC）
     */
    public Web3j getWeb3j(String chainType) {
        BcApiKeys apiKey = apiKeyService.getAvailableKey(chainType);

        if (apiKey == null || StringUtils.isEmpty(apiKey.getApiKey())) {
            if (web3jCache!=null && web3jCache.size()>0){
                web3jCache.clear();
            }

            throw new RuntimeException("无可用的 " + chainType + " 节点");
        }

        Exception lastEx = null;

        Long apiKeyId=apiKey.getId();
        String url =this.buildApiUrl(apiKey);
        String cacheKey = chainType + ":" + apiKeyId;
        try {
            // 从缓存获取或创建新的 Web3j 实例
            Web3j web3j = web3jCache.computeIfAbsent(cacheKey,
                    k -> Web3j.build(new HttpService(url)));
            web3jToKeyId.put(web3j,apiKeyId);
            // 测试节点可用性（快速 ping 一次）
           /** String clientVersion = web3j.web3ClientVersion().send().getWeb3ClientVersion();

            if (clientVersion != null && !clientVersion.isEmpty()) {
                // 记录节点使用
                apiKeyService.recordUsage(apiKeyId);
                return web3j;
            }**/

            return web3j;

        } catch (Exception e) {
           lastEx = e;

            // 标记节点失败（或限流）
            // 移除缓存，防止下次继续用坏节点
            this.invalidateNode(chainType,apiKeyId);

        }

        throw new RuntimeException("所有 Web3 节点都不可用", lastEx);
    }

    /**
     * 手动清理某个节点的缓存（例如检测到节点状态变化）
     */
    public void invalidateNode(String chainType, Long apiKeyId) {
        String cacheKey = chainType + ":" + apiKeyId;
        web3jCache.remove(cacheKey);
    }

    /**
     * 更新接口调用的次数
     * @param web3j
     */
    public void setApiCount(Web3j web3j){
       Long apiKeyId= web3jToKeyId.get(web3j);
        apiKeyService.recordUsage(apiKeyId);
    }
    /**
     * 构建 API URL（添加 API Key）
     */
    private String buildApiUrl(BcApiKeys apiKey) {
        String url = apiKey.getApiUrl();

        /**
        // Infura 风格: https://mainnet.infura.io/v3/{projectId}
        if (url.contains("infura.io")) {
            if (!url.endsWith("/")) {
                url += "/";
            }
            url += apiKey.getApiKey();
        }

        // Alchemy 风格: https://eth-mainnet.g.alchemy.com/v2/{apiKey}
        else if (url.contains("alchemy.com")) {
            if (!url.endsWith("/")) {
                url += "/";
            }
            url += apiKey.getApiKey();
        }**/
        if (!url.endsWith("/")) {
            url += "/";
        }
        url += apiKey.getApiKey();

        return url;
    }
}
