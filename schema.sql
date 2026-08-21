-- ==========================================================================
-- Personal Blog 数据库全套表结构定义与初始化脚本
-- 兼容 前台(personal-web) / 后台管理端 / Spring Boot 后端(personal-backend)
-- ==========================================================================

CREATE DATABASE IF NOT EXISTS `personal_blog` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `personal_blog`;

-- 1. 用户表 (后台管理员及鉴权)
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '登录账号',
  `password` VARCHAR(200) NOT NULL COMMENT 'BCrypt加密密码',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '用户昵称',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像地址',
  `role` VARCHAR(20) DEFAULT 'ROLE_ADMIN' COMMENT '角色权限',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户与管理员表';

-- 2. 分类表
CREATE TABLE IF NOT EXISTS `category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(100) NOT NULL UNIQUE COMMENT '分类名称',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '分类描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章分类表';

-- 3. 标签表
CREATE TABLE IF NOT EXISTS `tag` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '标签ID',
  `name` VARCHAR(100) NOT NULL UNIQUE COMMENT '标签名称',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签表';

-- 4. 文章主体表
CREATE TABLE IF NOT EXISTS `article` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `title` VARCHAR(200) NOT NULL COMMENT '文章标题',
  `summary` TEXT DEFAULT NULL COMMENT '文章摘要',
  `content` LONGTEXT DEFAULT NULL COMMENT 'Markdown文章正文',
  `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
  `category_id` BIGINT DEFAULT NULL COMMENT '所属分类ID',
  `is_featured` TINYINT(1) DEFAULT 0 COMMENT '是否置顶(1=置顶, 0=普通)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_is_featured` (`is_featured`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章主体表';

-- 5. 文章-标签关联表
CREATE TABLE IF NOT EXISTS `article_tag` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `article_id` BIGINT NOT NULL COMMENT '文章ID',
  `tag_id` BIGINT NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`id`),
  KEY `idx_article_id` (`article_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签关联表';

-- 6. 随笔动态表 (Moments / Essays)
CREATE TABLE IF NOT EXISTS `moment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '动态ID',
  `content` TEXT NOT NULL COMMENT '动态内容',
  `tag` VARCHAR(50) DEFAULT '生活随笔' COMMENT '动态标签',
  `likes` INT DEFAULT 0 COMMENT '点赞数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='随笔动态表';

-- 7. 动态多媒体关联表 (图片/视频)
CREATE TABLE IF NOT EXISTS `moment_media` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `moment_id` BIGINT NOT NULL COMMENT '动态ID',
  `media_type` VARCHAR(20) DEFAULT 'image' COMMENT '类型: image/video',
  `url` VARCHAR(500) NOT NULL COMMENT '资源URL',
  PRIMARY KEY (`id`),
  KEY `idx_moment_id` (`moment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态多媒体表';

-- 8. 开源作品集表 (Projects)
CREATE TABLE IF NOT EXISTS `project` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `name` VARCHAR(100) NOT NULL COMMENT '项目名称',
  `description` TEXT DEFAULT NULL COMMENT '项目描述',
  `content` LONGTEXT DEFAULT NULL COMMENT '详细介绍',
  `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '项目封面',
  `github_url` VARCHAR(500) DEFAULT NULL COMMENT 'GitHub仓库地址',
  `download_url` VARCHAR(500) DEFAULT NULL COMMENT '下载链接/Demo链接',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='开源作品集表';

-- 9. 留言板评论表 (Messages / Guestbook)
CREATE TABLE IF NOT EXISTS `message` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '留言ID',
  `nickname` VARCHAR(50) DEFAULT '匿名访客' COMMENT '留言者昵称',
  `content` TEXT NOT NULL COMMENT '留言正文',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `website` VARCHAR(200) DEFAULT NULL COMMENT '个人网址',
  `color` VARCHAR(20) DEFAULT NULL COMMENT '气泡色彩',
  `rotation` INT DEFAULT 0 COMMENT '微倾斜角',
  `likes` INT DEFAULT 0 COMMENT '点赞数',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '留言时间',
  PRIMARY KEY (`id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留言板表';

-- 10. 站点全局设置表
CREATE TABLE IF NOT EXISTS `site_setting` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `setting_key` VARCHAR(100) NOT NULL UNIQUE COMMENT '设置键',
  `setting_value` TEXT DEFAULT NULL COMMENT '设置值',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站点设置表';
