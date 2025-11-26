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
import com.ruoyi.blockchain.domain.TokenPrices;
import com.ruoyi.blockchain.service.ITokenPricesService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 币种配置Controller
 * 
 * @author dc
 * @date 2025-10-29
 */
@Controller
@RequestMapping("/config/tokens")
public class TokenPricesController extends BaseController
{
    private String prefix = "config/tokens";

    @Autowired
    private ITokenPricesService tokenPricesService;

    @RequiresPermissions("config:tokens:view")
    @GetMapping()
    public String tokens()
    {
        return prefix + "/tokens";
    }

    /**
     * 查询币种配置列表
     */
    @RequiresPermissions("config:tokens:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(TokenPrices tokenPrices)
    {
        startPage();
        List<TokenPrices> list = tokenPricesService.selectTokenPricesList(tokenPrices);
        return getDataTable(list);
    }

    /**
     * 导出币种配置列表
     */
    @RequiresPermissions("config:tokens:export")
    @Log(title = "币种配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(TokenPrices tokenPrices)
    {
        List<TokenPrices> list = tokenPricesService.selectTokenPricesList(tokenPrices);
        ExcelUtil<TokenPrices> util = new ExcelUtil<TokenPrices>(TokenPrices.class);
        return util.exportExcel(list, "币种配置数据");
    }

    /**
     * 新增币种配置
     */
    @RequiresPermissions("config:tokens:add")
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存币种配置
     */
    @RequiresPermissions("config:tokens:add")
    @Log(title = "币种配置", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(TokenPrices tokenPrices)
    {
        return toAjax(tokenPricesService.insertTokenPrices(tokenPrices));
    }

    /**
     * 修改币种配置
     */
    @RequiresPermissions("config:tokens:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        TokenPrices tokenPrices = tokenPricesService.selectTokenPricesById(id);
        mmap.put("tokenPrices", tokenPrices);
        return prefix + "/edit";
    }

    /**
     * 修改保存币种配置
     */
    @RequiresPermissions("config:tokens:edit")
    @Log(title = "币种配置", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(TokenPrices tokenPrices)
    {
        return toAjax(tokenPricesService.updateTokenPrices(tokenPrices));
    }

    /**
     * 删除币种配置
     */
    @RequiresPermissions("config:tokens:remove")
    @Log(title = "币种配置", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(tokenPricesService.deleteTokenPricesByIds(ids));
    }
}
