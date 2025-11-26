package com.ruoyi.bc.service.impl;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ruoyi.bc.domain.TokenPriceData;
import com.ruoyi.bc.service.IPriceSourceAdapter;
import com.ruoyi.blockchain.domain.TokenPrices;
import com.ruoyi.blockchain.service.ITokenPricesService;
import com.ruoyi.common.utils.http.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * OKX 价格源适配器
 */
@Service("okxPriceAdapter")
public class OkxPriceAdapter implements IPriceSourceAdapter {
    private static final String ApiUrl="https://www.okx.com/api/v5";
    private static final Logger log = LoggerFactory.getLogger(OkxPriceAdapter.class);

    private Map<String, String> SYMBOL_MAP=new HashMap<>();
    @Autowired
    private ITokenPricesService iTokenPricesService;

    /**
     * 交易对映射：tokenSymbol -> OKX InstId
     *         private Map<String, String> instIdMapping = new HashMap<String, String>() {{
     *             put("TRX", "TRX-USDT");
     *             put("ETH", "ETH-USDT");
     *             put("BNB", "BNB-USDT");
     *             put("BTC", "BTC-USDT");
     *         }};
     * @return
     */
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
            SYMBOL_MAP.put(t.getTokenSymbol(),t.getTokenSymbol()+"-USDT");
        }
        return SYMBOL_MAP;


    }
    @Override
    public String getSourceName() {
        return "OKX";
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

                String instId = mapping.get(tokenSymbol);
                String url = String.format("%s/market/ticker?instId=%s",
                        ApiUrl, instId);

                //log.debug("OKX 请求: {}", url);

                String response = HttpUtils.sendGet(url);
                JSONObject json = JSON.parseObject(response);

                if ("0".equals(json.getString("code"))) {
                    JSONArray data = json.getJSONArray("data");
                    if (data != null && !data.isEmpty()) {
                        JSONObject ticker = data.getJSONObject(0);
                        BigDecimal price = ticker.getBigDecimal("last");
                        results.add(TokenPriceData.success(tokenSymbol, price, getSourceName()));
                       // log.info("✅ {} 价格: ${} (OKX)", tokenSymbol, price);
                    } else {
                        results.add(TokenPriceData.failure(tokenSymbol, "数据为空"));
                    }
                } else {
                    results.add(TokenPriceData.failure(tokenSymbol, json.getString("msg")));
                }
            }

        } catch (Exception e) {
            log.error("OKX 获取价格失败", e);
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