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
import com.ruoyi.blockchain.domain.Platforms;
import com.ruoyi.blockchain.service.IPlatformsService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 下游平台Controller
 * 
 * @author dc
 * @date 2025-10-27
 */
@Controller
@RequestMapping("/platforms/platform")
public class PlatformsController extends BaseController
{
    private String prefix = "platforms/platform";

    @Autowired
    private IPlatformsService platformsService;

    @RequiresPermissions("platforms:platform:view")
    @GetMapping()
    public String platform()
    {
        return prefix + "/platform";
    }

    /**
     * 查询下游平台列表
     */
    @RequiresPermissions("platforms:platform:list")
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(Platforms platforms)
    {
        startPage();
        List<Platforms> list = platformsService.selectPlatformsList(platforms);
        return getDataTable(list);
    }



    /**
     * 新增下游平台
     */
    @RequiresPermissions("platforms:platform:add")
    @GetMapping("/add")
    public String add()
    {
        return prefix + "/add";
    }

    /**
     * 新增保存下游平台
     */
    @RequiresPermissions("platforms:platform:add")
    @Log(title = "下游平台", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(Platforms platforms)
    {
        platforms.setCreateBy(getLoginName());
        return toAjax(platformsService.insertPlatforms(platforms));
    }

    /**
     * 修改下游平台
     */
    @RequiresPermissions("platforms:platform:edit")
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, ModelMap mmap)
    {
        Platforms platforms = platformsService.selectPlatformsById(id);
        mmap.put("platforms", platforms);
        return prefix + "/edit";
    }

    /**
     * 修改保存下游平台
     */
    @RequiresPermissions("platforms:platform:edit")
    @Log(title = "下游平台", businessType = BusinessType.UPDATE)
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(Platforms platforms)
    {
        platforms.setUpdateBy(getLoginName());
        return toAjax(platformsService.updatePlatforms(platforms));
    }

    /**
     * 删除下游平台
     */
    @RequiresPermissions("platforms:platform:remove")
    @Log(title = "下游平台", businessType = BusinessType.DELETE)
    @PostMapping( "/remove")
    @ResponseBody
    public AjaxResult remove(String ids)
    {
        return toAjax(platformsService.deletePlatformsByIds(ids));
    }
}
