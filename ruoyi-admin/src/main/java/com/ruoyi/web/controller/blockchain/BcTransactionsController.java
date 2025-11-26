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
import com.ruoyi.blockchain.domain.BcTransactions;
import com.ruoyi.blockchain.service.IBcTransactionsService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 地址充值记录Controller
 * 
 * @author dc
 * @date 2025-10-30
 */
@Controller
@RequestMapping("/trade/recharge")
public class BcTransactionsController extends BaseController
{
    private String prefix = "trade/recharge";

    @Autowired
    private IBcTransactionsService bcTransactionsService;

    @RequiresPermissions("trade:recharge:view")
    @GetMapping()
    public String recharge()
    {
        return prefix + "/recharge";
    }

    /**
     * 查询地址充值记录列表
     */
    @RequiresPermissions("trade:recharge:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(BcTransactions bcTransactions)
    {
        startPage();
        List<BcTransactions> list = bcTransactionsService.selectBcTransactionsList(bcTransactions);
        return getDataTable(list);
    }

    /**
     * 导出地址充值记录列表
     */
    @RequiresPermissions("trade:recharge:export")
    @Log(title = "地址充值记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(BcTransactions bcTransactions)
    {
        List<BcTransactions> list = bcTransactionsService.selectBcTransactionsList(bcTransactions);
        ExcelUtil<BcTransactions> util = new ExcelUtil<BcTransactions>(BcTransactions.class);
        return util.exportExcel(list, "地址充值记录数据");
    }

    /**
     * 新增地址充值记录
     */
    @RequiresPermissions("trade:recharge:add")
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存地址充值记录
     */
    @RequiresPermissions("trade:recharge:add")
    @Log(title = "地址充值记录", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(BcTransactions bcTransactions)
    {
        return toAjax(1);
        //return toAjax(bcTransactionsService.insertBcTransactions(bcTransactions));
    }

    /**
     * 修改地址充值记录
     */
    @RequiresPermissions("trade:recharge:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        BcTransactions bcTransactions = bcTransactionsService.selectBcTransactionsById(id);
        mmap.put("bcTransactions", bcTransactions);
        return prefix + "/edit";
    }

    /**
     * 修改保存地址充值记录
     */
    @RequiresPermissions("trade:recharge:edit")
    @Log(title = "地址充值记录", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(BcTransactions bcTransactions)
    {
        return toAjax(1);
        //return toAjax(bcTransactionsService.updateBcTransactions(bcTransactions));
    }

    /**
     * 删除地址充值记录
     */
    @RequiresPermissions("trade:recharge:remove")
    @Log(title = "地址充值记录", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(1);
        //return toAjax(bcTransactionsService.deleteBcTransactionsByIds(ids));
    }
}
