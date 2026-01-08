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
import com.ruoyi.blockchain.domain.BcEnergyPaymentConfig;
import com.ruoyi.blockchain.service.IBcEnergyPaymentConfigService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 能量API配置Controller
 * 
 * @author dc
 * @date 2026-01-08
 */
@Controller
@RequestMapping("/config/energy")
public class BcEnergyPaymentConfigController extends BaseController
{
    private String prefix = "config/energy";

    @Autowired
    private IBcEnergyPaymentConfigService bcEnergyPaymentConfigService;
    @Autowired
    private IPlatformsService platformsService;

    @RequiresPermissions("config:energy:view")
    @GetMapping()
    public String energy()
    {
        return prefix + "/energy";
    }

    /**
     * 查询能量API配置列表
     */
    @RequiresPermissions("config:energy:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(BcEnergyPaymentConfig bcEnergyPaymentConfig)
    {
        startPage();
        List<BcEnergyPaymentConfig> list = bcEnergyPaymentConfigService.selectBcEnergyPaymentConfigList(bcEnergyPaymentConfig);
        return getDataTable(list);
    }

    /**
     * 导出能量API配置列表
     */
    @RequiresPermissions("config:energy:export")
    @Log(title = "能量API配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(BcEnergyPaymentConfig bcEnergyPaymentConfig)
    {
        List<BcEnergyPaymentConfig> list = bcEnergyPaymentConfigService.selectBcEnergyPaymentConfigList(bcEnergyPaymentConfig);
        ExcelUtil<BcEnergyPaymentConfig> util = new ExcelUtil<BcEnergyPaymentConfig>(BcEnergyPaymentConfig.class);
        return util.exportExcel(list, "能量API配置数据");
    }

    /**
     * 新增能量API配置
     */
    @RequiresPermissions("config:energy:add")
    @GetMapping("/add")
    public String add(ModelMap mmap)
    {
        mmap.put("platforms", platformsService.getPlatformsAll());
        return prefix + "/add";
    }

    /**
     * 新增保存能量API配置
     */
    @RequiresPermissions("config:energy:add")
    @Log(title = "能量API配置", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(BcEnergyPaymentConfig bcEnergyPaymentConfig)
    {
        return toAjax(bcEnergyPaymentConfigService.insertBcEnergyPaymentConfig(bcEnergyPaymentConfig));
    }

    /**
     * 修改能量API配置
     */
    @RequiresPermissions("config:energy:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        BcEnergyPaymentConfig bcEnergyPaymentConfig = bcEnergyPaymentConfigService.selectBcEnergyPaymentConfigById(id);
        mmap.put("bcEnergyPaymentConfig", bcEnergyPaymentConfig);
        mmap.put("platforms", platformsService.getPlatformsAll());
        return prefix + "/edit";
    }

    /**
     * 修改保存能量API配置
     */
    @RequiresPermissions("config:energy:edit")
    @Log(title = "能量API配置", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(BcEnergyPaymentConfig bcEnergyPaymentConfig)
    {
        return toAjax(bcEnergyPaymentConfigService.updateBcEnergyPaymentConfig(bcEnergyPaymentConfig));
    }

    /**
     * 删除能量API配置
     */
    @RequiresPermissions("config:energy:remove")
    @Log(title = "能量API配置", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(bcEnergyPaymentConfigService.deleteBcEnergyPaymentConfigByIds(ids));
    }
}
