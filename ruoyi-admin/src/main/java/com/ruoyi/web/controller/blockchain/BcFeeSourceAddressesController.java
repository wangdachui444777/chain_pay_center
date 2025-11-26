package com.ruoyi.web.controller.blockchain;

import java.util.List;

import com.ruoyi.blockchain.service.IPlatformsService;
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
import com.ruoyi.blockchain.domain.BcFeeSourceAddresses;
import com.ruoyi.blockchain.service.IBcFeeSourceAddressesService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 付款地址Controller
 * 
 * @author dc
 * @date 2025-11-09
 */
@Controller
@RequestMapping("/collection/sourceAddress")
public class BcFeeSourceAddressesController extends BaseController
{
    private String prefix = "collection/sourceAddress";

    @Autowired
    private IBcFeeSourceAddressesService bcFeeSourceAddressesService;

    @Autowired
    private IPlatformsService platformsService;

    @RequiresPermissions("collection:sourceAddress:view")
    @GetMapping()
    public String sourceAddress()
    {
        return prefix + "/sourceAddress";
    }

    /**
     * 查询付款地址列表
     */
    @RequiresPermissions("collection:sourceAddress:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(BcFeeSourceAddresses bcFeeSourceAddresses)
    {
        startPage();
        List<BcFeeSourceAddresses> list = bcFeeSourceAddressesService.selectBcFeeSourceAddressesList(bcFeeSourceAddresses);
        return getDataTable(list);
    }

    /**
     * 导出付款地址列表
     */
    @RequiresPermissions("collection:sourceAddress:export")
    @Log(title = "付款地址", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(BcFeeSourceAddresses bcFeeSourceAddresses)
    {
        List<BcFeeSourceAddresses> list = bcFeeSourceAddressesService.selectBcFeeSourceAddressesList(bcFeeSourceAddresses);
        ExcelUtil<BcFeeSourceAddresses> util = new ExcelUtil<BcFeeSourceAddresses>(BcFeeSourceAddresses.class);
        return util.exportExcel(list, "付款地址数据");
    }

    /**
     * 新增付款地址
     */
    @RequiresPermissions("collection:sourceAddress:add")
    @GetMapping("/add")
    public String add(ModelMap mmap)
    {
        mmap.put("platforms", platformsService.getPlatformsAll());
        return prefix + "/add";
    }

    /**
     * 新增保存付款地址
     */
    @RequiresPermissions("collection:sourceAddress:add")
    @Log(title = "付款地址", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(BcFeeSourceAddresses bcFeeSourceAddresses)
    {
        bcFeeSourceAddresses.setCreateBy(getLoginName());
        return toAjax(bcFeeSourceAddressesService.insertBcFeeSourceAddresses(bcFeeSourceAddresses));
    }

    /**
     * 修改付款地址
     */
    @RequiresPermissions("collection:sourceAddress:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {

        BcFeeSourceAddresses bcFeeSourceAddresses = bcFeeSourceAddressesService.selectBcFeeSourceAddressesById(id);
        mmap.put("platforms", platformsService.getPlatformsAll());
        mmap.put("bcFeeSourceAddresses", bcFeeSourceAddresses);
        return prefix + "/edit";
    }

    /**
     * 修改保存付款地址
     */
    @RequiresPermissions("collection:sourceAddress:edit")
    @Log(title = "付款地址", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(BcFeeSourceAddresses bcFeeSourceAddresses)
    {
        bcFeeSourceAddresses.setUpdateBy(getLoginName());
        return toAjax(bcFeeSourceAddressesService.updateBcFeeSourceAddresses(bcFeeSourceAddresses));
    }

    /**
     * 删除付款地址
     */
    @RequiresPermissions("collection:sourceAddress:remove")
    @Log(title = "付款地址", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(bcFeeSourceAddressesService.deleteBcFeeSourceAddressesByIds(ids));
    }
}
