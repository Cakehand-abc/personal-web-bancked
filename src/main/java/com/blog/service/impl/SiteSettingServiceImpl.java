package com.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.SiteSetting;
import com.blog.mapper.SiteSettingMapper;
import com.blog.service.SiteSettingService;
import org.springframework.stereotype.Service;

@Service
public class SiteSettingServiceImpl extends ServiceImpl<SiteSettingMapper, SiteSetting> implements SiteSettingService {

    @Override
    public SiteSetting getSetting() {
        // 全局设置永远只有一条，ID 固定为 1
        return this.getById(1L);
    }

    @Override
    public void updateSetting(SiteSetting setting) {
        // 强制确保只更新 ID 为 1 的记录
        setting.setId(1L);
        this.updateById(setting);
    }
}
