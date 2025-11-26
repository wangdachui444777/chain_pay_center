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
import com.ruoyi.blockchain.domain.BcApiKeys;
import com.ruoyi.blockchain.service.IBcApiKeysService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 区块API配置Controller
 * 
 * @author dc
 * @date 2025-10-29
 */
@Controller
@RequestMapping("/config/apiKey")
public class BcApiKeysController extends BaseController
{
    private String prefix = "config/apiKey";

    @Autowired
    private IBcApiKeysService bcApiKeysService;

    @RequiresPermissions("config:apiKey:view")
    @GetMapping()
    public String apiKey()
    {
        return prefix + "/apiKey";
    }

    /**
     * 查询区块API配置列表
     */
    @RequiresPermissions("config:apiKey:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(BcApiKeys bcApiKeys)
    {
        startPage();
        List<BcApiKeys> list = bcApiKeysService.selectBcApiKeysList(bcApiKeys);
        return getDataTable(list);
    }

    /**
     * 导出区块API配置列表
     */
    @RequiresPermissions("config:apiKey:export")
    @Log(title = "区块API配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(BcApiKeys bcApiKeys)
    {
        List<BcApiKeys> list = bcApiKeysService.selectBcApiKeysList(bcApiKeys);
        ExcelUtil<BcApiKeys> util = new ExcelUtil<BcApiKeys>(BcApiKeys.class);
        return util.exportExcel(list, "区块API配置数据");
    }

    /**
     * 新增区块API配置
     */
    @RequiresPermissions("config:apiKey:add")
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存区块API配置
     */
    @RequiresPermissions("config:apiKey:add")
    @Log(title = "区块API配置", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(BcApiKeys bcApiKeys)
    {
        return toAjax(bcApiKeysService.insertBcApiKeys(bcApiKeys));
    }

    /**
     * 修改区块API配置
     */
    @RequiresPermissions("config:apiKey:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        BcApiKeys bcApiKeys = bcApiKeysService.selectBcApiKeysById(id);
        mmap.put("bcApiKeys", bcApiKeys);
        return prefix + "/edit";
    }

    /**
     * 修改保存区块API配置
     */
    @RequiresPermissions("config:apiKey:edit")
    @Log(title = "区块API配置", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(BcApiKeys bcApiKeys)
    {
        return toAjax(bcApiKeysService.updateBcApiKeys(bcApiKeys));
    }

    /**
     * 删除区块API配置
     */
    @RequiresPermissions("config:apiKey:remove")
    @Log(title = "区块API配置", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(bcApiKeysService.deleteBcApiKeysByIds(ids));
    }
}
