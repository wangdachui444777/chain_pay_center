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
import com.ruoyi.blockchain.domain.BcWithdrawRecord;
import com.ruoyi.blockchain.service.IBcWithdrawRecordService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 区块链提现记录Controller
 * 
 * @author dc
 * @date 2025-11-12
 */
@Controller
@RequestMapping("/trade/withdraw")
public class BcWithdrawRecordController extends BaseController
{
    private String prefix = "trade/withdraw";

    @Autowired
    private IBcWithdrawRecordService bcWithdrawRecordService;

    @RequiresPermissions("trade:withdraw:view")
    @GetMapping()
    public String withdraw()
    {
        return prefix + "/withdraw";
    }

    /**
     * 查询区块链提现记录列表
     */
    @RequiresPermissions("trade:withdraw:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(BcWithdrawRecord bcWithdrawRecord)
    {
        startPage();
        List<BcWithdrawRecord> list = bcWithdrawRecordService.selectBcWithdrawRecordList(bcWithdrawRecord);
        return getDataTable(list);
    }

    /**
     * 导出区块链提现记录列表
     */
    @RequiresPermissions("trade:withdraw:export")
    @Log(title = "区块链提现记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(BcWithdrawRecord bcWithdrawRecord)
    {
        List<BcWithdrawRecord> list = bcWithdrawRecordService.selectBcWithdrawRecordList(bcWithdrawRecord);
        ExcelUtil<BcWithdrawRecord> util = new ExcelUtil<BcWithdrawRecord>(BcWithdrawRecord.class);
        return util.exportExcel(list, "区块链提现记录数据");
    }

    /**
     * 新增区块链提现记录
     */
    @RequiresPermissions("trade:withdraw:add")
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存区块链提现记录
     */
    @RequiresPermissions("trade:withdraw:add")
    @Log(title = "区块链提现记录", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(BcWithdrawRecord bcWithdrawRecord)
    {
        return toAjax(1);
        //return toAjax(bcWithdrawRecordService.insertBcWithdrawRecord(bcWithdrawRecord));
    }

    /**
     * 修改区块链提现记录
     */
    @RequiresPermissions("trade:withdraw:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        BcWithdrawRecord bcWithdrawRecord = bcWithdrawRecordService.selectBcWithdrawRecordById(id);
        mmap.put("bcWithdrawRecord", bcWithdrawRecord);
        return prefix + "/edit";
    }

    /**
     * 修改保存区块链提现记录
     */
    @RequiresPermissions("trade:withdraw:edit")
    @Log(title = "区块链提现记录", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(BcWithdrawRecord bcWithdrawRecord)
    {
        return toAjax(1);
       // return toAjax(bcWithdrawRecordService.updateBcWithdrawRecord(bcWithdrawRecord));
    }

    /**
     * 删除区块链提现记录
     */
    @RequiresPermissions("trade:withdraw:remove")
    @Log(title = "区块链提现记录", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(1);
        //return toAjax(bcWithdrawRecordService.deleteBcWithdrawRecordByIds(ids));
    }
}
