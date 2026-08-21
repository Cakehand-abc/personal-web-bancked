package com.blog.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.entity.SiteSetting;
import com.blog.mapper.SiteSettingMapper;
import com.blog.service.SiteSettingService;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import jakarta.annotation.PostConstruct;

@Service
public class SiteSettingServiceImpl extends ServiceImpl<SiteSettingMapper, SiteSetting> implements SiteSettingService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        try {
            jdbcTemplate.execute("ALTER TABLE site_setting ADD COLUMN use_old_intro TINYINT(1) DEFAULT 0");
            System.out.println("成功添加 use_old_intro 列");
        } catch (Exception e) {
            System.out.println("检查 use_old_intro 列已存在或添加失败 (预期内)");
        }
        
        try {
            jdbcTemplate.execute("ALTER TABLE site_setting ADD COLUMN default_cover_url VARCHAR(255) DEFAULT ''");
            System.out.println("成功添加 default_cover_url 列");
        } catch (Exception e) {
            System.out.println("检查 default_cover_url 列已存在或添加失败 (预期内)");
        }
    }

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
