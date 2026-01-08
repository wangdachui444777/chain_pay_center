package com.ruoyi.bc.component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.ruoyi.blockchain.domain.BcConsolidationDetail;
import com.ruoyi.common.utils.http.HttpUtils;

/**
 * CatFee API客户端封装类
 * 用于TRON网络能量租赁相关操作
 */
public class CatFeeApiClient {

    //正式环境
    //private static final String BASE_URL = "https://api.catfee.io";
    //测试环境
    private  String BASE_URL;
    private static final String ORDER_ENDPOINT = "/v1/order";
    private static final String BALANCE_ENDPOINT = "/v1/balance";

    private final String apiKey;
    private final String apiSecret;
    private final ObjectMapper objectMapper;
    /**
     * 构造函数
     * @param apiKey API密钥
     * @param apiSecret API密钥对应的Secret
     */
    private CatFeeApiClient(String apiKey, String apiSecret,String baseUrl) {
        this.apiKey = apiKey.trim();
        this.apiSecret = apiSecret.trim();
        this.BASE_URL = baseUrl.trim();
        this.objectMapper = new ObjectMapper();
    }

    public static CatFeeApiClient create(String apiKey, String apiSecret, String baseUrl) {
        return new CatFeeApiClient(apiKey, apiSecret, baseUrl);
    }

    /**
     * 创建能量租赁订单
     * @param quantity 能量数量
     * @param receiver 接收地址（TRON地址）
     * @param duration 租赁时长（如：1h, 1d, 3d等）
     * @return API响应结果
     * @throws Exception 请求异常
     */
    public ApiResponse<OrderResult> createEnergyOrder(long quantity, String receiver, String duration)
            throws Exception {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("quantity", String.valueOf(quantity));
        params.put("receiver", receiver);
        params.put("duration", duration);
       // params.put("activate", false);
        String response = sendRequest("POST", ORDER_ENDPOINT, params);

        return parseResponse(response, OrderResult.class);
    }

    /**
     * 查询订单状态
     * @param orderId 订单ID
     * @return 订单详情
     * @throws Exception 请求异常
     */
    public ApiResponse<OrderDetail> getOrderDetail(String orderId) throws Exception {
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("id", orderId);
        String path=ORDER_ENDPOINT+"/"+orderId;

        String response = sendRequest("GET", path, null);
        return parseResponse(response, OrderDetail.class);
    }

    /**
     * 查询账户余额
     * @return 账户余额信息
     * @throws Exception 请求异常
     */
    public ApiResponse<BalanceInfo> getBalance() throws Exception {
        String response = sendRequest("GET", BALANCE_ENDPOINT, null);
        return parseResponse(response, BalanceInfo.class);
    }

    /**
     * 取消订单
     * @param orderId 订单ID
     * @return 取消结果
     * @throws Exception 请求异常
     */
    public ApiResponse<CancelResult> cancelOrder(String orderId) throws Exception {
        Map<String, Object> params = new HashMap<>();
        params.put("id", orderId);

        String response = sendRequest("DELETE", ORDER_ENDPOINT, params);
        return parseResponse(response, CancelResult.class);
    }



    /**
     * 生成当前UTC时间戳（ISO 8601格式）
     * @return 时间戳字符串
     */
    private String generateTimestamp() {

       Instant now = Instant.now();
        DateTimeFormatter formatter = DateTimeFormatter
                .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .withZone(ZoneOffset.UTC);

        String timestamp = formatter.format(now);
        return timestamp;//Instant.now().toString();
    }

    // 构建请求路径，包括查询参数
    public static String buildRequestPath(String path, Map<String, Object> queryParams) {
        if (queryParams == null || queryParams.isEmpty()) {
            return path;
        }
        String queryString = queryParams.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&", "?", ""));
        return path + queryString;
    }
    /**
     * 生成API签名
     * @param timestamp 时间戳
     * @param method HTTP方法
     * @param requestPath 请求路径
     * @return 签名字符串
     * @throws Exception 签名生成异常
     */
    private String generateSignature(String timestamp, String method, String requestPath)
            throws Exception {
        String signString = timestamp + method + requestPath;
        System.out.println(signString);
        return hmacSHA256(signString, apiSecret);
    }

    /**
     * 使用HMAC-SHA256算法生成签名
     * @param data 待签名数据
     * @param secret 签名密钥
     * @return Base64编码的签名
     * @throws Exception 签名异常
     */
    private String hmacSHA256(String data, String secret) throws Exception {
        Mac sha256Hmac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256Hmac.init(secretKey);
        byte[] hash = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
    /**
     * 发送HTTP请求的核心方法
     * @param method HTTP方法
     * @param endpoint 接口端点
     * @param queryParams 查询参数
     * @return 响应内容
     * @throws Exception 请求异常
     */
    private String sendRequest(String method, String endpoint, Map<String, Object> queryParams)
            throws Exception {
        // 生成时间戳
        String timestamp = generateTimestamp();

        String requestPath = buildRequestPath(endpoint, queryParams);
        // 生成签名
        String signature = generateSignature(timestamp, method, requestPath);

        // 构建完整URL
        String url = BASE_URL +requestPath;

        // 创建HTTP请求
        Map<String,String> headers=new HashMap<>();
        // headers.put("Content-Type","application/json");
        headers.put("CF-ACCESS-KEY",apiKey);
        headers.put("CF-ACCESS-SIGN",signature);
        headers.put("CF-ACCESS-TIMESTAMP",timestamp);

        String body=null;
        switch (method.toUpperCase()) {
            case "POST":
                body=HttpUtils.sendPostJsonMap(url, queryParams, headers);
                break;
            case "PUT":
                body=HttpUtils.sendPostJsonMap(url, queryParams, headers);
                break;
            case "DELETE":
                body=HttpUtils.sendGet(url,null,headers);
                break;
            case "GET":
                body=HttpUtils.sendGet(url,null,headers);
                break;
            default:
                throw new UnsupportedOperationException("Unsupported HTTP method: " + method);
        }
       //=HttpUtils.sendPostJsonMap(url, queryParams, headers);


        return body;
    }

    /**
     * 解析API响应
     * @param responseBody 响应内容
     * @param dataClass 数据类型
     * @return 解析后的响应对象
     * @throws Exception 解析异常
     */
    private <T> ApiResponse<T> parseResponse(String responseBody, Class<T> dataClass) throws Exception {
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        ApiResponse<T> apiResponse = new ApiResponse<>();
        if (jsonNode==null){
            return null;
        }
        if (!jsonNode.has("code")){
            return null;
        }
        int code=jsonNode.get("code").asInt();
        if (code==0){
            apiResponse.setSuccess(true);
            apiResponse.setMessage("success");
        }else{
            apiResponse.setSuccess(false);
            apiResponse.setMessage(jsonNode.get("msg").asText());
        }
        if (apiResponse.isSuccess() && jsonNode.has("data")) {
            T data = objectMapper.convertValue(jsonNode.get("data"), dataClass);
            apiResponse.setData(data);
        }

        return apiResponse;
    }

    // ======================= 数据类定义 =======================

    /**
     * API响应基础类
     */
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public T getData() { return data; }
        public void setData(T data) { this.data = data; }

        @Override
        public String toString() {
            return "ApiResponse{" +
                    "success=" + success +
                    ", message='" + message + '\'' +
                    ", data=" + data +
                    '}';
        }
    }

    /**
     * 订单创建结果
     */
    public static class OrderResult {
        private String id;
        @JsonProperty("resource_type")
        private String resourceType;
        @JsonProperty("billing_type")
        private String billingType;
        @JsonProperty("source_type")
        private String sourceType;
        @JsonProperty("pay_timestamp")
        private Long payTimestamp;
        private String status;
        private String receiver;
        private Integer duration;
        @JsonProperty("pay_amount_sun")
        private double payAmountSun;
        private Long quantity;
        @JsonProperty("activate_status")
        private String activateStatus;
        @JsonProperty("confirm_status")
        private String confirmStatus;
        @JsonProperty("activate_amount_sun")
        private Long activateAmountSun;
        private Long balance;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getResourceType() {
            return resourceType;
        }

        public void setResourceType(String resourceType) {
            this.resourceType = resourceType;
        }

        public String getBillingType() {
            return billingType;
        }

        public void setBillingType(String billingType) {
            this.billingType = billingType;
        }

        public String getSourceType() {
            return sourceType;
        }

        public void setSourceType(String sourceType) {
            this.sourceType = sourceType;
        }

        public Long getPayTimestamp() {
            return payTimestamp;
        }

        public void setPayTimestamp(Long payTimestamp) {
            this.payTimestamp = payTimestamp;
        }

        public Long getActivateAmountSun() {
            return activateAmountSun;
        }

        public void setActivateAmountSun(Long activateAmountSun) {
            this.activateAmountSun = activateAmountSun;
        }


        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getReceiver() {
            return receiver;
        }

        public void setReceiver(String receiver) {
            this.receiver = receiver;
        }

        public Integer getDuration() {
            return duration;
        }

        public void setDuration(Integer duration) {
            this.duration = duration;
        }

        public double getPayAmountSun() {
            return payAmountSun;
        }

        public void setPayAmountSun(double payAmountSun) {
            this.payAmountSun = payAmountSun;
        }

        public Long getQuantity() {
            return quantity;
        }

        public void setQuantity(Long quantity) {
            this.quantity = quantity;
        }

        public String getActivateStatus() {
            return activateStatus;
        }

        public void setActivateStatus(String activateStatus) {
            this.activateStatus = activateStatus;
        }

        public String getConfirmStatus() {
            return confirmStatus;
        }

        public void setConfirmStatus(String confirmStatus) {
            this.confirmStatus = confirmStatus;
        }

        public Long getBalance() {
            return balance;
        }

        public void setBalance(Long balance) {
            this.balance = balance;
        }

        @Override
        public String toString() {
            return "OrderResult{" +
                    "id='" + id + '\'' +
                    ", resourceType='" + resourceType + '\'' +
                    ", billingType='" + billingType + '\'' +
                    ", sourceType='" + sourceType + '\'' +
                    ", payTimestamp=" + payTimestamp +
                    ", status='" + status + '\'' +
                    ", receiver='" + receiver + '\'' +
                    ", duration=" + duration +
                    ", payAmountSun=" + payAmountSun +
                    ", quantity=" + quantity +
                    ", activateStatus='" + activateStatus + '\'' +
                    ", confirmStatus='" + confirmStatus + '\'' +
                    ", balance=" + balance +
                    '}';
        }
    }

    /**
     * 订单详情
     */
    public static class OrderDetail extends OrderResult {
        @JsonProperty("delegate_hash")
        private String delegateHash;
        @JsonProperty("delegate_timestamp")
        private String delegateTimestamp;

        public String getDelegateHash() {
            return delegateHash;
        }

        public void setDelegateHash(String delegateHash) {
            this.delegateHash = delegateHash;
        }

        public String getDelegateTimestamp() {
            return delegateTimestamp;
        }

        public void setDelegateTimestamp(String delegateTimestamp) {
            this.delegateTimestamp = delegateTimestamp;
        }


        @Override
        public String toString() {
            return "OrderDetail{" +
                    "id='" + super.id + '\'' +
                    ", resourceType='" + super.resourceType + '\'' +
                    ", billingType='" + super.billingType + '\'' +
                    ", sourceType='" + super.sourceType + '\'' +
                    ", payTimestamp=" + super.payTimestamp +
                    ", status='" + super.status + '\'' +
                    ", receiver='" + super.receiver + '\'' +
                    ", duration=" + super.duration +
                    ", payAmountSun=" + super.payAmountSun +
                    ", quantity=" + super.quantity +
                    ", activateStatus='" + super.activateStatus + '\'' +
                    ", confirmStatus='" + super.confirmStatus + '\'' +
                    ", activateAmountSun=" + super.activateAmountSun +
                    ", balance=" + super.balance +
                    ", delegateHash='" + delegateHash + '\'' +
                    ", delegateTimestamp='" + delegateTimestamp + '\'' +
                    '}';
        }
    }

    /**
     * 余额信息
     */
    public static class BalanceInfo {
        private double balance;
        private String currency;
        private double frozenBalance;

        // Getters and Setters
        public double getBalance() { return balance; }
        public void setBalance(double balance) { this.balance = balance; }

        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }

        public double getFrozenBalance() { return frozenBalance; }
        public void setFrozenBalance(double frozenBalance) { this.frozenBalance = frozenBalance; }

        @Override
        public String toString() {
            return "BalanceInfo{" +
                    "balance=" + balance +
                    ", currency='" + currency + '\'' +
                    ", frozenBalance=" + frozenBalance +
                    '}';
        }
    }

    /**
     * 取消订单结果
     */
    public static class CancelResult {
        private String orderId;
        private boolean cancelled;
        private String reason;

        // Getters and Setters
        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }

        public boolean isCancelled() { return cancelled; }
        public void setCancelled(boolean cancelled) { this.cancelled = cancelled; }

        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }

        @Override
        public String toString() {
            return "CancelResult{" +
                    "orderId='" + orderId + '\'' +
                    ", cancelled=" + cancelled +
                    ", reason='" + reason + '\'' +
                    '}';
        }
    }

}
