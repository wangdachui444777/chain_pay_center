package com.ruoyi.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.common.utils.block.BcSignatureUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@RestController
@RequestMapping("/api/test")
public class TestApiController {
    private final static List<UserModel> users = new ArrayList<UserModel>();

    {
        users.add(new UserModel(1, "1000001", "测试1", "15888888888"));
        users.add(new UserModel(2, "1000002", "测试2", "15666666666"));
        users.add(new UserModel(3, "1000003", "测试3", "15666666666"));
        users.add(new UserModel(4, "1000004", "测试4", "15666666666"));
        users.add(new UserModel(5, "1000005", "测试5", "15666666666"));
    }

    @Anonymous
    @RequestMapping("/getUser")
    public R getUser() {
        return R.ok(users, "成功");
    }

    @Anonymous
    @RequestMapping("/updateUser")
    public R updateUser(Integer uid) {

        return R.ok("传入参数uid=" + uid);
    }

    /**
     * 平台密钥（需要与bc_platforms表中的secret_key一致）
     */
    private static final String SECRET_KEY = "F33ABehPffYtPlOK79c167462fdd7f37";
    private static final Logger log = LoggerFactory.getLogger(TestApiController.class);

    /**
     * 接收区块链交易回调通知
     * <p>
     * 接口地址示例：http://localhost:8080/test/api/callback
     *
     * @param callbackData 回调数据
     * @return 响应结果
     */
    @Anonymous
    @PostMapping("/callback")
    public String receiveCallback(CallbackData callbackData) {

        log.info("==================== 收到回调通知 ====================");
        log.info("回调数据: {}", callbackData);

        try {
            // 1. 验证必要参数
            if (callbackData == null ) {
                log.error("回调数据为空");
                return "FAIL: 回调数据为空";
            }

            // 2. 提取签名
            String receivedSign =callbackData.getSign();
            if (receivedSign == null || receivedSign.isEmpty()) {
                log.error("签名为空");
                return "FAIL: 签名为空";
            }
            Map<String, Object> map= new ObjectMapper().convertValue(callbackData, Map.class);
            // 3. 验证签名
            String calculatedSign = BcSignatureUtils.generateSign(map, SECRET_KEY);
            if (!receivedSign.equals(calculatedSign)) {
                log.error("签名验证失败！收到的签名: {}, 计算的签名: {}", receivedSign, calculatedSign);
                return "FAIL: 签名验证失败";
            }

            log.info("签名验证成功");

            // 4. 提取业务数据
            Long txId = callbackData.getTxId();
            String txHash = callbackData.getTxHash();
            String chainType = callbackData.getChainType();
            String tokenSymbol = callbackData.getTokenSymbol();
            String amount = callbackData.getAmount();
            Integer direction = callbackData.getDirection();
            String fromAddress = callbackData.getFromAddress();
            String toAddress = callbackData.getToAddress();

            log.info("==================== 交易信息 ====================");
            log.info("交易ID: {}", txId);
            log.info("交易哈希: {}", txHash);
            log.info("链类型: {}", chainType);
            log.info("币种: {}", tokenSymbol);
            log.info("金额: {}", amount);
            log.info("方向: {}", direction==1? "充值" : "归集");
            log.info("发送地址: {}", fromAddress);
            log.info("接收地址: {}", toAddress);
            log.info("================================================");

            // 5. 业务处理逻辑
            // TODO: 这里添加你的业务逻辑
            // 例如：
            // - 更新用户余额
            // - 记录充值记录
            // - 发送通知
            // - 等等...


                return "SUCCESS";

        } catch (Exception e) {
            return "FAIL: 处理异常 - " + e.getMessage();
        }
    }
    /**
     * 手动测试回调接口
     * 用于本地测试，无需签名验证
     *
     * 访问地址：http://localhost:8080/test/api/mockCallback
     *
     * @return 响应结果
     */
    @Anonymous
    @GetMapping("/mockCallback")
    public String mockCallback() {
        log.info("==================== 模拟回调测试 ====================");

        // 模拟回调数据
        Map<String, Object> mockData = new TreeMap<>();
        mockData.put("txId", 123456L);
        //交易状态 （1待完成，2已完成，3失败）
        mockData.put("txStatus", "2");
        mockData.put("txHash", "0x1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef");
        mockData.put("chainType", "TRX");
        mockData.put("tokenSymbol", "USDT");
        mockData.put("tokenContract", "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t");
        mockData.put("amount", "100.500000000000000000");
        mockData.put("direction", "1");
        mockData.put("fromAddress", "TXxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
        mockData.put("toAddress", "TYyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy");
        mockData.put("blockNumber", 12345678L);
        mockData.put("confirmations", 20);
        mockData.put("confirmedTime", "2024-01-01 12:00:00");
        mockData.put("timestamp", System.currentTimeMillis()/1000);

        // 生成签名
        String sign = BcSignatureUtils.generateSign(mockData, SECRET_KEY);
        mockData.put("sign", sign);

       // log.info("模拟数据: {}", mockData);
        CallbackData callbackData=new ObjectMapper().convertValue(mockData, CallbackData.class);
        //CallbackData callbackData=BeanUtil.mapToBean(mockData,CallbackData.class,true, CopyOptions.create());
        // 调用回调接口
        return receiveCallback(callbackData);
    }

    /**
     * 安全地将Object转换为Long
     */
    private Long getLong(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Long) {
            return (Long) obj;
        }
        if (obj instanceof Integer) {
            return ((Integer) obj).longValue();
        }
        if (obj instanceof String) {
            try {
                return Long.parseLong((String) obj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
class CallbackData {

    private Long txId;
    private String txHash;
    private String requestNo;

    private Integer txStatus;

    private String chainType;

    private String tokenSymbol;

    private String tokenContract;

    private String amount;

    private Integer direction;

    private String fromAddress;

    private String toAddress;

    private Long blockNumber;

    private Integer confirmations;

    private String confirmedTime;

    private Long timestamp;

    private String sign;

    public Long getTxId() {
        return txId;
    }

    public void setTxId(Long txId) {
        this.txId = txId;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public Integer getTxStatus() {
        return txStatus;
    }

    public void setTxStatus(Integer txStatus) {
        this.txStatus = txStatus;
    }

    public String getChainType() {
        return chainType;
    }

    public void setChainType(String chainType) {
        this.chainType = chainType;
    }

    public String getTokenSymbol() {
        return tokenSymbol;
    }

    public void setTokenSymbol(String tokenSymbol) {
        this.tokenSymbol = tokenSymbol;
    }

    public String getTokenContract() {
        return tokenContract;
    }

    public void setTokenContract(String tokenContract) {
        this.tokenContract = tokenContract;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public Long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(Long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public Integer getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(Integer confirmations) {
        this.confirmations = confirmations;
    }

    public String getConfirmedTime() {
        return confirmedTime;
    }

    public void setConfirmedTime(String confirmedTime) {
        this.confirmedTime = confirmedTime;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
class UserModel
{
    /** 用户ID */
    private int userId;

    /** 用户编号 */
    private String userCode;

    /** 用户姓名 */
    private String userName;

    /** 用户手机 */
    private String userPhone;

    public UserModel()
    {

    }

    public UserModel(int userId, String userCode, String userName, String userPhone)
    {
        this.userId = userId;
        this.userCode = userCode;
        this.userName = userName;
        this.userPhone = userPhone;
    }

    public int getUserId()
    {
        return userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    public String getUserCode()
    {
        return userCode;
    }

    public void setUserCode(String userCode)
    {
        this.userCode = userCode;
    }

    public String getUserName()
    {
        return userName;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

    public String getUserPhone()
    {
        return userPhone;
    }

    public void setUserPhone(String userPhone)
    {
        this.userPhone = userPhone;
    }

}
