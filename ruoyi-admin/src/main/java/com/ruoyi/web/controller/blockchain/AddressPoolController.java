package com.ruoyi.web.controller.blockchain;

import java.util.List;
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
import com.ruoyi.blockchain.domain.AddressPool;
import com.ruoyi.blockchain.service.IAddressPoolService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 临时地址Controller
 * 
 * @author dc
 * @date 2025-10-28
 */
@Controller
@RequestMapping("/platforms/pool")
public class AddressPoolController extends BaseController
{
    private String prefix = "platforms/pool";

    @Autowired
    private IAddressPoolService addressPoolService;

    @RequiresPermissions("platforms:pool:view")
    @GetMapping()
    public String pool()
    {
        return prefix + "/pool";
    }

    /**
     * 查询临时地址列表
     */
    @RequiresPermissions("platforms:pool:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(AddressPool addressPool)
    {
        startPage();
        List<AddressPool> list = addressPoolService.selectAddressPoolList(addressPool);
        return getDataTable(list);
    }

    /**
     * 导出临时地址列表
     */
    @RequiresPermissions("platforms:pool:export")
    @Log(title = "临时地址", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(AddressPool addressPool)
    {
        List<AddressPool> list = addressPoolService.selectAddressPoolList(addressPool);
        ExcelUtil<AddressPool> util = new ExcelUtil<AddressPool>(AddressPool.class);
        return util.exportExcel(list, "临时地址数据");
    }

    /**
     * 新增临时地址
     */
    @RequiresPermissions("platforms:pool:add")
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存临时地址
     */
    @RequiresPermissions("platforms:pool:add")
    @Log(title = "临时地址", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(AddressPool addressPool)
    {
        return toAjax(1);
        //return toAjax(addressPoolService.insertAddressPool(addressPool));
    }

    /**
     * 修改临时地址
     */
    @RequiresPermissions("platforms:pool:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        AddressPool addressPool = addressPoolService.selectAddressPoolById(id);
        mmap.put("addressPool", addressPool);
        return prefix + "/edit";
    }

    /**
     * 修改保存临时地址
     */
    @RequiresPermissions("platforms:pool:edit")
    @Log(title = "临时地址", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(AddressPool addressPool)
    {
        return toAjax(1);
       // return toAjax(addressPoolService.updateAddressPool(addressPool));
    }

    /**
     * 删除临时地址
     */
    @RequiresPermissions("platforms:pool:remove")
    @Log(title = "临时地址", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {

        return toAjax(addressPoolService.deleteAddressPoolByIds(ids));
    }
}
