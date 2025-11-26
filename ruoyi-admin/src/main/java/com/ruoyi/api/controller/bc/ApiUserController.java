package com.ruoyi.api.controller.bc;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.api.domian.*;
import com.ruoyi.blockchain.domain.*;
import com.ruoyi.blockchain.service.*;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.bean.BeanUtils;
import com.ruoyi.common.utils.block.BcSignatureUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Api("用户相关接口")
@RestController("用户接口")
@RequestMapping("/api/user")
public class ApiUserController {

    private IUserAddressesService iUserAddressesService;

    @Autowired
    public void setiUserAddressesService(IUserAddressesService iUserAddressesService) {
        this.iUserAddressesService = iUserAddressesService;
    }

    private IAddressPoolService iAddressPoolService;

    @Autowired
    public void setiAddressPoolService(IAddressPoolService iAddressPoolService) {
        this.iAddressPoolService = iAddressPoolService;
    }

    private IPlatformsService iPlatformsService;
    @Autowired
    public void setiPlatformsService(IPlatformsService iPlatformsService) {
        this.iPlatformsService = iPlatformsService;
    }
    private ITokenPricesService tokenPricesService;
    @Autowired
    public void setTokenPricesService(ITokenPricesService tokenPricesService) {
        this.tokenPricesService = tokenPricesService;
    }
    private IBcWithdrawRecordService iWithdrawRecordService;

    @Autowired
    public void setiWithdrawRecordService(IBcWithdrawRecordService iWithdrawRecordService) {
        this.iWithdrawRecordService = iWithdrawRecordService;
    }
    private int s=500;
    @Anonymous
    @ApiOperation("获取地址")
    @PostMapping("/getAddress")
    public AjaxResult getAddress(@Validated UserAddressReq req) {
        Long pId = req.getPlatformId();

        //校验签名
        Map<String, Object> params = new HashMap<>();
        params.put("platformId", pId);
        params.put("timestamp", req.getTimestamp());
        String chainType = null;
        String uuid = "";
        if (StringUtils.isNotEmpty(req.getChainType())) {
            chainType = req.getChainType();
            params.put("chainType", chainType);
        }
        if (StringUtils.isNotEmpty(req.getUuid())) {
            params.put("uuid", req.getUuid());
            uuid = req.getUuid();
        }

        try {
            //1.先判断平台是否存在
            Platforms platform = checkPlatfrom(pId);
            String secretKey = platform.getSecretKey();
            BcSignatureUtils.verifySignature(params, req.getSign(), secretKey, s);
        } catch (SecurityException e) {
            return AjaxResult.error("签名验证失败: " + e.getMessage());
        }catch (ServiceException e){
            return AjaxResult.error(e.getMessage());
        }
        //判断地址是否存在
        if (StringUtils.isNotEmpty(uuid)) {
            UserAddresses userAddresses=new UserAddresses();
            userAddresses.setPlatformId(pId);
            userAddresses.setUserId(uuid);
            if (StringUtils.isNotEmpty(chainType)) {
                userAddresses.setChainType(chainType);
            }
            List<UserAddresses> userAddressesList=iUserAddressesService.selectUserAddressesList(userAddresses);
            if (userAddressesList.size()>0) {
                List<UserAddressVo> voList1 = new ArrayList<>();
                for (UserAddresses ua : userAddressesList) {
                    UserAddressVo vo = new UserAddressVo();
                    BeanUtils.copyProperties(ua, vo); // bean -> vo 按字段名拷贝
                    voList1.add(vo);
                }
                return AjaxResult.success(voList1);
            }



        }
        //获取临时地址，获取到立马删除
        List<AddressPool> pools = iAddressPoolService.selectByChainTypes(chainType);
        if (pools.isEmpty()) {
            return AjaxResult.error("暂无可用地址，请稍后再试");
        }
        //插入用户地址表（这个函数中，实现插入redis）
        int flag = iUserAddressesService.batchInsertUserAddresses(pools, pId, uuid);
        if (flag > 0) {
            List<UserAddressVo> voList = new ArrayList<>();
            for (AddressPool pool : pools) {
                UserAddressVo vo = new UserAddressVo();
                BeanUtils.copyProperties(pool, vo); // bean -> vo 按字段名拷贝
                voList.add(vo);
            }

            return AjaxResult.success(voList);
        }
        return AjaxResult.error();
    }

    @Anonymous
    @ApiOperation("获取区块配置")
    @PostMapping("/getChainInfo")
    public AjaxResult getChainInfo(@Validated BaseEntityReq req){
        Long pId = req.getPlatformId();
        try {
            //1.先判断平台是否存在
            Platforms platform = checkPlatfrom(pId);
            //校验签名
            Map<String, Object> params = new HashMap<>();
            params.put("platformId", pId);
            params.put("timestamp", req.getTimestamp());
            String secretKey = platform.getSecretKey();
            BcSignatureUtils.verifySignature(params, req.getSign(), secretKey, s);
        } catch (SecurityException e) {
            return AjaxResult.error("签名验证失败: " + e.getMessage());
        } catch (ServiceException e){
            return AjaxResult.error(e.getMessage());
        }
        TokenPrices query=new TokenPrices();

       // List<ChainInfoVo> chainInfoList=new ArrayList<>();
        List<TokenPrices> tokenList=tokenPricesService.selectTokenPricesList(query);

        List<ChainInfoVo> chainInfoList = tokenList.stream().map(token -> {
            ChainInfoVo vo = new ChainInfoVo();
            BeanUtils.copyProperties(token, vo); // 自动拷贝同名字段
            return vo;
        }).collect(Collectors.toList());
        return AjaxResult.success(chainInfoList);
    }
    @Anonymous
    @ApiOperation("申请提现")
    @PostMapping("/appleWithdrawal")
    public AjaxResult appleWithdrawal(@Validated AppleWithdrawReq req){
        Long pId = req.getPlatformId();
        try {
        //1.先判断平台是否存在
        Platforms platform = checkPlatfrom(pId);
        //校验签名
       /** Map<String, Object> params = new HashMap<>();
        params.put("platformId", pId);
        params.put("timestamp", req.getTimestamp());**/
           TokenPrices tokenPrices= tokenPricesService.getTokenCacheByChain(req.getChainType(),req.getTokenSymbol());
           if (tokenPrices==null){
               return AjaxResult.error("区块/币种参数不匹配");
           }
            Map<String, Object> params= new ObjectMapper().convertValue(req, Map.class);
            String secretKey = platform.getSecretKey();
            BcSignatureUtils.verifySignature(params, req.getSign(), secretKey, s);
            //处理数据
            BcWithdrawRecord addData=BeanUtils.instantiateClass(BcWithdrawRecord.class);
            BeanUtils.copyBeanProp(addData,req);
            addData.setTokenContract(tokenPrices.getTokenContract());
            iWithdrawRecordService.insertBcWithdrawRecord(addData);
        } catch (SecurityException e) {
            return AjaxResult.error("签名验证失败: " + e.getMessage());
        }catch (ServiceException e){
            return AjaxResult.error("提交订单失败:"+e.getMessage());
        }
        return AjaxResult.success();

    }

    private Platforms checkPlatfrom(Long platformId){

        if (platformId==null){
            throw new ServiceException("platformId 不能为空");
        }
        //1.先判断平台是否存在
        Platforms platform = iPlatformsService.selectPlatformsById(platformId);
        if (platform == null || "1".equals(platform.getStatus())) {
            throw new ServiceException("平台不存在或被禁用");
        }
        return platform;
    }

}
