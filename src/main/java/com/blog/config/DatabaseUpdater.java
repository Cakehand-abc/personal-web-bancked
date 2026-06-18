package com.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseUpdater implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            // 尝试添加 github_id 列，如果已经存在会抛异常，忽略即可
            jdbcTemplate.execute("ALTER TABLE `sys_user` ADD COLUMN `github_id` VARCHAR(100) NULL COMMENT 'GitHub唯一标识'");
            System.out.println("========== 数据库自动升级：成功添加 github_id 字段 ==========");
        } catch (Exception e) {
            System.out.println("========== 数据库自动升级：github_id 字段已存在，跳过 ==========");
        }

        try {
            // 尝试添加 avatar 列
            jdbcTemplate.execute("ALTER TABLE `sys_user` ADD COLUMN `avatar` VARCHAR(255) NULL COMMENT '头像地址'");
            System.out.println("========== 数据库自动升级：成功添加 avatar 字段 ==========");
        } catch (Exception e) {
            System.out.println("========== 数据库自动升级：avatar 字段已存在，跳过 ==========");
        }

        try {
            // 自动创建网站设置表
            String createSettingTable = "CREATE TABLE IF NOT EXISTS `site_setting` (" +
                    "  `id` BIGINT NOT NULL AUTO_INCREMENT," +
                    "  `intro_media_type` VARCHAR(50) DEFAULT 'image' COMMENT '开场媒体类型: video 或 image'," +
                    "  `intro_media_url` VARCHAR(255) DEFAULT NULL COMMENT '开场媒体的访问链接'," +
                    "  `site_name` VARCHAR(100) DEFAULT 'My Blog' COMMENT '网站名称'," +
                    "  `site_signature` VARCHAR(255) DEFAULT 'Hello World' COMMENT '站长签名'," +
                    "  `record_number` VARCHAR(100) DEFAULT NULL COMMENT 'ICP备案号'," +
                    "  PRIMARY KEY (`id`)" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网站全局设置表'";
            jdbcTemplate.execute(createSettingTable);
            System.out.println("========== 数据库自动升级：确保 site_setting 表存在 ==========");
            
            // 如果表里没数据，初始化一条默认数据（ID必须为1，因为全局设置只有一条）
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM `site_setting`", Integer.class);
            if (count != null && count == 0) {
                jdbcTemplate.execute("INSERT INTO `site_setting` (`id`, `intro_media_type`, `site_name`) VALUES (1, 'image', 'Cakehand 的个人博客')");
                System.out.println("========== 数据库自动升级：已初始化默认全局设置 ==========");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
