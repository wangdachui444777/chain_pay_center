package com.ruoyi.bc.service.impl;




import com.alibaba.fastjson.JSONObject;
import com.ruoyi.bc.domain.TokenPriceData;
import com.ruoyi.bc.service.IPriceSourceAdapter;
import com.ruoyi.blockchain.domain.TokenPrices;
import com.ruoyi.blockchain.service.ITokenPricesService;
import com.ruoyi.common.json.JSON;
import com.ruoyi.common.utils.http.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Binance 价格源适配器
 */
@Service("binancePriceAdapter")
public class BinancePriceAdapter implements IPriceSourceAdapter {
    private static final String ApiUrl="https://api.binance.com/api/v3";
    private static final Logger log = LoggerFactory.getLogger(BinancePriceAdapter.class);

    private Map<String, String> SYMBOL_MAP=new HashMap<>();
    @Autowired
    private ITokenPricesService iTokenPricesService;
    @Override
    public String getSourceName() {
        return "Binance";
    }
    private Map<String, String> getSymbolMapping(){
        if (SYMBOL_MAP.size()>0){
            return SYMBOL_MAP;
        }
        TokenPrices wprices=new TokenPrices();
        wprices.setEnabled(1);
        List<TokenPrices> tokens = iTokenPricesService.selectTokenPricesList(wprices);
        Set<String> seenSymbols = new HashSet<>();
        List<TokenPrices> pricesList = tokens.stream()
                // 去重：只保留第一个相同 symbol 的
                .filter(t -> seenSymbols.add(t.getTokenSymbol().toUpperCase()))
                .collect(Collectors.toList());
        for (TokenPrices t:pricesList) {
            SYMBOL_MAP.put(t.getTokenSymbol(),t.getTokenSymbol()+"USDT");
        }
        return SYMBOL_MAP;
        /** 交易对映射：tokenSymbol -> Binance Symbol
        private Map<String, String> symbolMapping = new HashMap<String, String>() {{
            put("TRX", "TRXUSDT");
            put("ETH", "ETHUSDT");
            put("BNB", "BNBUSDT");
            put("BTC", "BTCUSDT");
            // USDT 和 USDC 价格固定为 1
        }};*/

    }
    @Override
    public List<TokenPriceData> fetchPrices(List<String> tokenSymbols) {
        List<TokenPriceData> results = new ArrayList<>();

        try {
            Map<String, String> mapping = getSymbolMapping();

            for (String tokenSymbol : tokenSymbols) {
                // USDT 和 USDC 固定价格
                if ("USDT".equals(tokenSymbol) || "USDC".equals(tokenSymbol)) {
                    results.add(TokenPriceData.success(tokenSymbol, BigDecimal.ONE, getSourceName()));
                    continue;
                }

                if (!isSupported(tokenSymbol)) {
                    results.add(TokenPriceData.failure(tokenSymbol, "不支持的代币"));
                    continue;
                }

                String symbol = mapping.get(tokenSymbol);
                String url = String.format("%s/ticker/price?symbol=%s",
                        ApiUrl, symbol);

                //log.debug("📡 Binance 请求: {}", url);

                String response = HttpUtils.sendGet(url);
                JSONObject json =JSONObject.parseObject(response);

                if (json.containsKey("price")) {
                    BigDecimal price = json.getBigDecimal("price");
                    results.add(TokenPriceData.success(tokenSymbol, price, getSourceName()));
                   // log.info("✅ {} 价格: ${} (Binance)", tokenSymbol, price);
                } else {
                    results.add(TokenPriceData.failure(tokenSymbol, "未找到价格"));
                }
            }

        } catch (Exception e) {
            log.error(" Binance 获取价格失败", e);
            for (String token : tokenSymbols) {
                results.add(TokenPriceData.failure(token, e.getMessage()));
            }
        }

        return results;
    }

    @Override
    public TokenPriceData fetchPrice(String tokenSymbol) {
        List<TokenPriceData> results = fetchPrices(List.of(tokenSymbol));
        return results.isEmpty() ? TokenPriceData.failure(tokenSymbol, "获取失败") : results.get(0);
    }



    @Override
    public boolean isSupported(String tokenSymbol) {
        if ("USDT".equals(tokenSymbol) || "USDC".equals(tokenSymbol)) {
            return true;
        }
        return getSymbolMapping().containsKey(tokenSymbol);
    }
}