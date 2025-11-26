package com.ruoyi.web.controller.blockchain;

import java.util.List;

import com.ruoyi.blockchain.domain.AddressBalances;
import com.ruoyi.blockchain.service.IAddressBalancesService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.blockchain.domain.UserAddresses;
import com.ruoyi.blockchain.service.IUserAddressesService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 用户地址Controller
 * 
 * @author dc
 * @date 2025-10-27
 */
@Controller
@RequestMapping("/platforms/addresses")
public class UserAddressesController extends BaseController
{
    private String prefix = "platforms/addresses";

    @Autowired
    private IUserAddressesService userAddressesService;
    @Autowired
    private IAddressBalancesService balancesService;

    @RequiresPermissions("platforms:addresses:view")
    @GetMapping()
    public String addresses()
    {
        return prefix + "/addresses";
    }

    /**
     * 查询用户地址列表
     */
    @RequiresPermissions("platforms:addresses:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(UserAddresses userAddresses)
    {
        startPage();
        List<UserAddresses> list = userAddressesService.selectUserAddressesList(userAddresses);
        return getDataTable(list);
    }

    /**
     * 导出用户地址列表
     */
    @RequiresPermissions("platforms:addresses:export")
    @Log(title = "用户地址", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(UserAddresses userAddresses)
    {
        List<UserAddresses> list = userAddressesService.selectUserAddressesList(userAddresses);
        ExcelUtil<UserAddresses> util = new ExcelUtil<UserAddresses>(UserAddresses.class);
        return util.exportExcel(list, "用户地址数据");
    }

    /**
     * 新增用户地址
     */
    @RequiresPermissions("platforms:addresses:add")
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存用户地址
     */
    @RequiresPermissions("platforms:addresses:add")
    @Log(title = "用户地址", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(UserAddresses userAddresses)
    {
        return toAjax(1);
        //return toAjax(userAddressesService.insertUserAddresses(userAddresses));
    }

    /**
     * 修改用户地址
     */
    @RequiresPermissions("platforms:addresses:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        UserAddresses userAddresses = userAddressesService.selectUserAddressesById(id);
        mmap.put("userAddresses", userAddresses);
        return prefix + "/edit";
    }
    /**
     * 地址余额处理
     */
    @RequiresPermissions("platforms:addresses:list")
    @GetMapping("/view/{id}")
    public String view(@PathVariable("id") Long id, ModelMap mmap)
    {
        //UserAddresses userAddresses = userAddressesService.selectUserAddressesById(id);
        mmap.put("addressId", id);
        return prefix + "/balance";
    }

    /**
     * 地址余额列表
     */
    @RequiresPermissions("platforms:addresses:list")
    @PostMapping("/balanceList")
    @ResponseBody
    public TableDataInfo balanceList(AddressBalances userAddresses)
    {
        startPage();
        List<AddressBalances> list = balancesService.selectAddressBalancesList(userAddresses);
        return getDataTable(list);
    }
    /**
     * 修改保存用户地址
     */
    @RequiresPermissions("platforms:addresses:edit")
    @Log(title = "用户地址", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(UserAddresses userAddresses)
    {
        return toAjax(1);
        //return toAjax(userAddressesService.updateUserAddresses(userAddresses));
    }

    /**
     * 删除用户地址
     */
    @RequiresPermissions("platforms:addresses:remove")
    @Log(title = "用户地址", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(1);
        //return toAjax(userAddressesService.deleteUserAddressesByIds(ids));
    }
}
