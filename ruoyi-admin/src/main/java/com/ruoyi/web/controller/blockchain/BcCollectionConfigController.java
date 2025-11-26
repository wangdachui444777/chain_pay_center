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
import com.ruoyi.blockchain.domain.BcCollectionConfig;
import com.ruoyi.blockchain.service.IBcCollectionConfigService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 归集配置Controller
 * 
 * @author dc
 * @date 2025-11-09
 */
@Controller
@RequestMapping("/collection/config")
public class BcCollectionConfigController extends BaseController
{
    private String prefix = "collection/config";

    @Autowired
    private IBcCollectionConfigService bcCollectionConfigService;
    @Autowired
    private IPlatformsService platformsService;

    @RequiresPermissions("collection:config:view")
    @GetMapping()
    public String config()
    {
        return prefix + "/config";
    }

    /**
     * 查询归集配置列表
     */
    @RequiresPermissions("collection:config:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(BcCollectionConfig bcCollectionConfig)
    {
        startPage();
        List<BcCollectionConfig> list = bcCollectionConfigService.selectBcCollectionConfigList(bcCollectionConfig);
        return getDataTable(list);
    }

    /**
     * 导出归集配置列表
     */
    @RequiresPermissions("collection:config:export")
    @Log(title = "归集配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(BcCollectionConfig bcCollectionConfig)
    {
        List<BcCollectionConfig> list = bcCollectionConfigService.selectBcCollectionConfigList(bcCollectionConfig);
        ExcelUtil<BcCollectionConfig> util = new ExcelUtil<BcCollectionConfig>(BcCollectionConfig.class);
        return util.exportExcel(list, "归集配置数据");
    }

    /**
     * 新增归集配置
     */
    @RequiresPermissions("collection:config:add")
    @GetMapping("/add")
    public String add(ModelMap mmap)
    {
        mmap.put("platforms", platformsService.getPlatformsAll());
        return prefix + "/add";
    }

    /**
     * 新增保存归集配置
     */
    @RequiresPermissions("collection:config:add")
    @Log(title = "归集配置", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(BcCollectionConfig bcCollectionConfig)
    {
        return toAjax(bcCollectionConfigService.insertBcCollectionConfig(bcCollectionConfig));
    }

    /**
     * 修改归集配置
     */
    @RequiresPermissions("collection:config:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        BcCollectionConfig bcCollectionConfig = bcCollectionConfigService.selectBcCollectionConfigById(id);
        mmap.put("bcCollectionConfig", bcCollectionConfig);
        mmap.put("platforms", platformsService.getPlatformsAll());
        return prefix + "/edit";
    }

    /**
     * 修改保存归集配置
     */
    @RequiresPermissions("collection:config:edit")
    @Log(title = "归集配置", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(BcCollectionConfig bcCollectionConfig)
    {
        return toAjax(bcCollectionConfigService.updateBcCollectionConfig(bcCollectionConfig));
    }

    /**
     * 删除归集配置
     */
    @RequiresPermissions("collection:config:remove")
    @Log(title = "归集配置", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(bcCollectionConfigService.deleteBcCollectionConfigByIds(ids));
    }
}
