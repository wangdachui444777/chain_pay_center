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
import com.ruoyi.blockchain.domain.BcConsolidationDetail;
import com.ruoyi.blockchain.service.IBcConsolidationDetailService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 归集记录Controller
 * 
 * @author dc
 * @date 2025-11-09
 */
@Controller
@RequestMapping("/trade/collection")
public class BcConsolidationDetailController extends BaseController
{
    private String prefix = "trade/collection";

    @Autowired
    private IBcConsolidationDetailService bcConsolidationDetailService;

    @RequiresPermissions("trade:collection:view")
    @GetMapping()
    public String collection()
    {
        return prefix + "/collection";
    }

    /**
     * 查询归集记录列表
     */
    @RequiresPermissions("trade:collection:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(BcConsolidationDetail bcConsolidationDetail)
    {
        startPage();
        List<BcConsolidationDetail> list = bcConsolidationDetailService.selectBcConsolidationDetailList(bcConsolidationDetail);
        return getDataTable(list);
    }

    /**
     * 导出归集记录列表
     */
    @RequiresPermissions("trade:collection:export")
    @Log(title = "归集记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    @ResponseBody
    public AjaxResult export(BcConsolidationDetail bcConsolidationDetail)
    {
        List<BcConsolidationDetail> list = bcConsolidationDetailService.selectBcConsolidationDetailList(bcConsolidationDetail);
        ExcelUtil<BcConsolidationDetail> util = new ExcelUtil<BcConsolidationDetail>(BcConsolidationDetail.class);
        return util.exportExcel(list, "归集记录数据");
    }

    /**
     * 新增归集记录
     */
    @RequiresPermissions("trade:collection:add")
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存归集记录
     */
    @RequiresPermissions("trade:collection:add")
    @Log(title = "归集记录", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(BcConsolidationDetail bcConsolidationDetail)
    {
        return toAjax(bcConsolidationDetailService.insertBcConsolidationDetail(bcConsolidationDetail));
    }

    /**
     * 修改归集记录
     */
    @RequiresPermissions("trade:collection:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        BcConsolidationDetail bcConsolidationDetail = bcConsolidationDetailService.selectBcConsolidationDetailById(id);
        mmap.put("bcConsolidationDetail", bcConsolidationDetail);
        return prefix + "/edit";
    }

    /**
     * 修改保存归集记录
     */
    @RequiresPermissions("trade:collection:edit")
    @Log(title = "归集记录", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(BcConsolidationDetail bcConsolidationDetail)
    {
        return toAjax(1);
       // return toAjax(bcConsolidationDetailService.updateBcConsolidationDetail(bcConsolidationDetail));
    }

    /**
     * 删除归集记录
     */
    @RequiresPermissions("trade:collection:remove")
    @Log(title = "归集记录", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(1);
       // return toAjax(bcConsolidationDetailService.deleteBcConsolidationDetailByIds(ids));
    }
}
