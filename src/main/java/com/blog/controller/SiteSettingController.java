package com.blog.controller;

import com.blog.common.Result;
import com.blog.entity.SiteSetting;
import com.blog.service.SiteSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SiteSettingController {

    @Autowired
    private SiteSettingService siteSettingService;

    /**
     * 前台无需 Token 即可获取网站设置（包括开场视频链接）
     */
    @GetMapping("/settings")
    public Result<SiteSetting> getSettings() {
        SiteSetting setting = siteSettingService.getSetting();
        return Result.success(setting);
    }

    /**
     * 后台管理端更新网站设置（拦截器会自动校验 Token）
     */
    @PutMapping("/settings/admin")
    public Result<String> updateSettings(@RequestBody SiteSetting setting) {
        siteSettingService.updateSetting(setting);
        return Result.success("网站全局设置更新成功！");
    }
}
