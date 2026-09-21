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

        try {
            jdbcTemplate.execute("ALTER TABLE site_setting ADD COLUMN avatar_url VARCHAR(255) DEFAULT ''");
            System.out.println("成功添加 avatar_url 列");
        } catch (Exception e) {
            System.out.println("检查 avatar_url 列已存在或添加失败 (预期内)");
        }

        try {
            jdbcTemplate.execute("ALTER TABLE moment ADD COLUMN tag VARCHAR(50) DEFAULT '生活随笔'");
            System.out.println("成功为 moment 添加 tag 列");
        } catch (Exception e) {
            System.out.println("检查 moment.tag 列已存在或无需添加");
        }

        try {
            jdbcTemplate.execute("ALTER TABLE moment ADD COLUMN likes INT DEFAULT 0");
            System.out.println("成功为 moment 添加 likes 列");
        } catch (Exception e) {
            System.out.println("检查 moment.likes 列已存在或无需添加");
        }

        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS moment_media (" +
                    "id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "moment_id BIGINT NOT NULL, " +
                    "media_type VARCHAR(20) DEFAULT 'image', " +
                    "url VARCHAR(500) NOT NULL, " +
                    "KEY idx_moment_id (moment_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            System.out.println("检查 moment_media 表完成");
        } catch (Exception e) {
            System.out.println("检查 moment_media 表失败: " + e.getMessage());
        }

        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS gallery_album (" +
                    "id VARCHAR(64) NOT NULL PRIMARY KEY, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "description VARCHAR(500) DEFAULT '', " +
                    "location VARCHAR(100) DEFAULT '', " +
                    "album_date VARCHAR(50) DEFAULT '', " +
                    "cover VARCHAR(500) DEFAULT '', " +
                    "raw_tags VARCHAR(255) DEFAULT '', " +
                    "sort_order INT DEFAULT 0, " +
                    "create_time DATETIME DEFAULT CURRENT_TIMESTAMP) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            System.out.println("检查 gallery_album 表完成");
        } catch (Exception e) {
            System.out.println("检查 gallery_album 表失败: " + e.getMessage());
        }

        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS gallery_photo (" +
                    "id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "album_id VARCHAR(64) NOT NULL, " +
                    "url VARCHAR(500) NOT NULL, " +
                    "title VARCHAR(100) DEFAULT '', " +
                    "description VARCHAR(500) DEFAULT '', " +
                    "sort_order INT DEFAULT 0, " +
                    "create_time DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                    "KEY idx_album_id (album_id)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4");
            System.out.println("检查 gallery_photo 表完成");
        } catch (Exception e) {
            System.out.println("检查 gallery_photo 表失败: " + e.getMessage());
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
