package com.ruoyi.bc.service.impl;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.blockchain.domain.TokenPrices;
import com.ruoyi.blockchain.domain.TronTransaction;
import com.ruoyi.blockchain.service.ITokenPricesService;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.http.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tron.trident.utils.Numeric;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TRON API 服务（支持数据库配置和多 API Key）
 */
@Service
public class TronScanApiService {
    private static final Logger log = LoggerFactory.getLogger(TronScanApiService.class);

    @Autowired
    private ITokenPricesService tokenPriceService;


    /** TronScan API */
    private static final String TRONSCAN_API = "https://apilist.tronscanapi.com/api";
    private static final String API_KEY="f2d31447-3dc5-4801-a953-5fda7e0d7ca1";

    /** 合约类型常量 */
    private static final int TRANSFER_CONTRACT = 1;              // TRX转账
    private static final int TRANSFER_ASSET_CONTRACT = 2;        // TRC10转账
    private static final int TRIGGER_SMART_CONTRACT = 31;        // 智能合约调用(TRC20)
    private static final int DELEGATE_RESOURCE_CONTRACT = 57;    // 资源委托
    private static final String CHAIN_TYPE="TRX";
    @PostConstruct
    public void init() {
        tokenPriceService.initTokenConfigCache(CHAIN_TYPE);
        log.info("========================================");
        log.info("{} API2 服务初始化完成",CHAIN_TYPE);
        log.info("========================================");
    }





    /**
     * 获取最新区块号
     */
    public Long getLatestBlockNumber() {
        try {
            String url = TRONSCAN_API+"/block?sort=-number&limit=1";
            String response = HttpUtils.sendGet(url);
            JSONObject json = JSON.parseObject(response);
            JSONArray data = json.getJSONArray("data");

            if (data != null && !data.isEmpty()) {
                return data.getJSONObject(0).getLong("number");
            }
            return 0l;
        } catch (Exception e) {
            log.error("获取最新区块号失败", e);
        }
        return 0L;
    }

    /**
     * 获取指定区块信息
     */
    public JSONObject getBlockByBock(int limit,int offset, Long blockNumber) {
        // 构造查询URL

        String url = String.format("%s/transaction?block=%d&limit=%d&start=%d&sort=-timestamp",
                TRONSCAN_API, blockNumber, limit, offset);
        try {
            //log.debug("请求 TronScan: url {}",url);
           // String response = HttpUtil.get(url, 10000);
           // return JSONUtil.parseObj(response);
            Map<String, String> headers = new HashMap<>();
            // 添加 API Key 到请求头
            headers.put("TRON-PRO-API-KEY",API_KEY);
            String body=HttpUtils.sendGet(url,null,headers);
            return JSON.parseObject(body);

        } catch (Exception e) {
            log.error("获取区块 {}  失败",url, e);
            return null;
        }
    }
    /**
     * 解析区块中的交易
     */
    public List<TronTransaction> parseBlockTransactions(JSONObject jsonObject) {
        List<TronTransaction> transactions = new ArrayList<>();

        try {
            // 获取交易列表
            JSONArray txList = jsonObject.getJSONArray("data");
            if (txList == null || txList.isEmpty()) {
                return transactions;
            }

            for (int i = 0; i < txList.size(); i++) {
                JSONObject tx = txList.getJSONObject(i);

                TronTransaction parsedTx = parseTransaction(tx);
                if (parsedTx != null) {
                    transactions.add(parsedTx);
                }
            }

        } catch (Exception e) {
            log.error("解析交易数据失败", e);
        }

        return transactions;
    }

    /**
     * 解析交易
     */
    private TronTransaction parseTransaction(JSONObject tx) {
        try {
            String txHash = tx.getString("hash");
            Long blockNumber = tx.getLong("block");
            Long timestamp = tx.getLong("timestamp");
            Integer contractType = tx.getInteger("contractType");
            String result = tx.getString("result");
            Boolean revert = tx.getBoolean("revert");

            // 跳过失败的交易
            if (!"SUCCESS".equals(result)) {
                log.debug("跳过失败交易: {}, result: {}", txHash, result);
                return null;
            }
            // 跳过回滚的交易
            if (Boolean.TRUE.equals(revert)) {
                log.debug("跳过未确认或回滚交易: {}", txHash);
                return null;
            }
            //log.info("tx 数据{}",tx);

            // 根据合约类型解析
            switch (contractType) {
                case TRANSFER_CONTRACT:
                    // TRX转账
                    return parseTrxTransfer(tx, txHash, blockNumber, timestamp);
                case TRIGGER_SMART_CONTRACT:
                    // TRC20转账
                    return parseTrc20FromLogs(tx, txHash, blockNumber, timestamp);

                default:
                    // 其他类型不处理（如资源委托等）
                    return null;
            }

        } catch (Exception e) {
            log.error("解析交易失败", e);
            return null;
        }
    }

    /**
     * 解析 TRX 转账
     */
    private TronTransaction parseTrxTransfer(JSONObject tx, String txHash,
                                             Long blockNumber, Long timestamp) {
        try {

            // 获取主币配置
            TokenPrices trxConfig = tokenPriceService.getTokenCacheByChain(CHAIN_TYPE,CHAIN_TYPE);
            if (trxConfig == null || trxConfig.getEnabled() != 1) {
                return null;
            }
            Boolean confirmed = tx.getBoolean("confirmed");
            String ownerAddress = tx.getString("ownerAddress");
            String toAddress = tx.getString("toAddress");
            Long amountSun = tx.getLong("amount");

            if (amountSun == null || amountSun <= 0) {
                return null;
            }

            // 转换金额（1 TRX = 10^6 SUN）
            BigDecimal amount = new BigDecimal(amountSun)
                    .divide(BigDecimal.TEN.pow(trxConfig.getDecimals()), 18, RoundingMode.DOWN);
            TronTransaction transaction = new TronTransaction();
            transaction.setTxHash(txHash);
            transaction.setBlockTimestamp(timestamp);
            transaction.setBlockNumber(blockNumber);
            transaction.setFromAddress(ownerAddress);
            transaction.setToAddress(toAddress);
            transaction.setAmount(amount);
            transaction.setTokenSymbol(trxConfig.getTokenSymbol());
            transaction.setTokenDecimals(trxConfig.getDecimals());
            transaction.setTxType(CHAIN_TYPE);
            //交易状态 （1待完成，2已完成，3失败
            transaction.setStatus( "2");
            transaction.setConfirmed(confirmed?"1":"0");
            return transaction;

        } catch (Exception e) {
            log.error("解析 TRX 转账失败: {}", txHash, e);
            return null;
        }
    }

    /**
     * 从原始日志解析（备用方案）
     */
    private TronTransaction parseTrc20FromLogs(JSONObject tx, String txHash,
                                               Long blockNumber, Long timestamp) {
        try {
            // 获取合约地址和发送方
            JSONObject contractData = tx.getJSONObject("contractData");
            String contractAddress = contractData.getString("contract_address");
            String fromAddress = tx.getString("ownerAddress");
            Boolean confirmed = tx.getBoolean("confirmed");
            // 从数据库获取代币配置
            TokenPrices tokenConfig = tokenPriceService.getTokenCacheByContractAddress(contractAddress);
            if (tokenConfig == null || tokenConfig.getEnabled() != 1) {
                return null;
            }
            String data = contractData.getString("data");
            if (data == null || data.length() < 8) {
                return null;
            }

            // 检查方法签名 (transfer方法: a9059cbb)
            String methodSignature = data.substring(0, 8);
            if (!"a9059cbb".equals(methodSignature)) {
                return null;  // 不是transfer方法
            }

            // 解析参数
            // data格式: a9059cbb + 64位to地址 + 64位金额
            if (data.length() < 136) {
                log.warn("data长度不足: {}", data);
                return null;
            }

            // 提取to地址 (从data中)
            //String toAddressHex = "41" + data.substring(32, 72).replaceFirst("^0+", "");
            String toAddressHex = "41" + data.substring(32, 72);
            String toAddress = hexAddressToBase58(toAddressHex);

            // 提取金额
            String amountHex = data.substring(72, 136);
            BigInteger amountWei = new BigInteger(amountHex, 16);
            BigDecimal amount = new BigDecimal(amountWei)
                    .divide(BigDecimal.TEN.pow(tokenConfig.getDecimals()), 18, RoundingMode.DOWN);

            //构造数据
            TronTransaction transaction = new TronTransaction();
            transaction.setTxHash(txHash);
            transaction.setBlockNumber(blockNumber);
            transaction.setFromAddress(fromAddress);
            transaction.setToAddress(toAddress);
            transaction.setAmount(amount);
            transaction.setTokenSymbol(tokenConfig.getTokenSymbol());
            transaction.setBlockTimestamp(timestamp);
            transaction.setTokenContract(contractAddress);
            transaction.setTokenDecimals(tokenConfig.getDecimals());
            transaction.setTxType("TRC20");
            //交易状态 （1待完成，2已完成，3失败
            transaction.setStatus("2");
            transaction.setConfirmed(confirmed?"1":"0");
            //log.debug("trc20解析: {} 转账: {} from {} to {}，txId {}",tokenConfig.getTokenSymbol(), amount, fromAddress, toAddress,txId);

            return transaction;

        } catch (Exception e) {
            log.error("从data解析TRC20转账失败: {}", txHash, e);
        }

        return null;
    }

    // ==================== 工具方法 ====================

    private String hexAddressToBase58(String hexAddress) {
        try {
            if (hexAddress.startsWith("41")) {
                return org.tron.trident.utils.Base58Check.bytesToBase58(
                        Numeric.hexStringToByteArray(hexAddress)
                );
            } else if (hexAddress.startsWith("0x")) {
                String hex = "41" + hexAddress.substring(2);
                return org.tron.trident.utils.Base58Check.bytesToBase58(
                        Numeric.hexStringToByteArray(hex)
                );
            }
            return hexAddress;
        } catch (Exception e) {
            log.error("地址转换失败: {}", hexAddress, e);
            return hexAddress;
        }
    }

    private String topicToAddress(String topic) {
        try {
            String addressHex = topic.substring(24);
            String fullHex = "41" + addressHex;

            return org.tron.trident.utils.Base58Check.bytesToBase58(
                    Numeric.hexStringToByteArray(fullHex)
            );
        } catch (Exception e) {
            log.error("Topic 转地址失败: {}", topic, e);
            return "";
        }
    }

}
