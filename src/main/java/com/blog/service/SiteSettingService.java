package com.blog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.blog.entity.SiteSetting;

public interface SiteSettingService extends IService<SiteSetting> {
    
    /**
     * 获取全局唯一设置（ID固定为1）
     */
    SiteSetting getSetting();

    /**
     * 更新全局设置
     */
    void updateSetting(SiteSetting setting);
}
