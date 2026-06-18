-- 1. 系统用户表
CREATE TABLE IF NOT EXISTS `sys_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '账号',
  `password` VARCHAR(100) NOT NULL COMMENT '密码',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
  `github_id` VARCHAR(100) DEFAULT NULL COMMENT 'GitHub唯一标识',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- 2. 分类表 (优先创建)
CREATE TABLE IF NOT EXISTS `category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- 3. 标签表 (优先创建)
CREATE TABLE IF NOT EXISTS `tag` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 4. 文章表 (依赖分类)
CREATE TABLE IF NOT EXISTS `article` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `title` VARCHAR(100) NOT NULL COMMENT '文章标题',
  `summary` VARCHAR(255) DEFAULT NULL COMMENT '文章摘要',
  `content` LONGTEXT NOT NULL COMMENT '文章正文(富文本)',
  `cover_url` VARCHAR(255) DEFAULT NULL COMMENT '封面图URL',
  `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
  `is_featured` TINYINT(1) DEFAULT 0 COMMENT '是否为编辑精选(1是 0否)',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发表时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章表';

-- 5. 文章标签关联表 (依赖文章和标签)
CREATE TABLE IF NOT EXISTS `article_tag` (
  `article_id` BIGINT NOT NULL,
  `tag_id` BIGINT NOT NULL,
  PRIMARY KEY (`article_id`, `tag_id`),
  KEY `idx_tag_id` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文章标签关联表';

-- 6. 动漫空间模块 - 动态主体表
CREATE TABLE IF NOT EXISTS `moment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `content` TEXT NOT NULL COMMENT '动态文字内容',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态主体表';

-- 7. 动漫空间模块 - 动态多媒体表 (依赖动态主体)
CREATE TABLE IF NOT EXISTS `moment_media` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `moment_id` BIGINT NOT NULL COMMENT '关联动态ID',
  `media_url` VARCHAR(255) NOT NULL COMMENT '媒体链接',
  `media_type` TINYINT NOT NULL COMMENT '类型: 1图片, 2视频',
  `sort_order` INT DEFAULT 0 COMMENT '排序权重',
  PRIMARY KEY (`id`),
  KEY `idx_moment_id` (`moment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态多媒体挂载表';

-- 8. 开源项目模块
CREATE TABLE IF NOT EXISTS `project` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL COMMENT '项目名称',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '简短描述',
  `content` LONGTEXT DEFAULT NULL COMMENT '详细富文本介绍',
  `cover_url` VARCHAR(255) DEFAULT NULL COMMENT '项目展示图',
  `github_url` VARCHAR(255) DEFAULT NULL COMMENT '源码链接',
  `download_url` VARCHAR(255) DEFAULT NULL COMMENT '下载链接',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='开源项目表';

-- 9. 网站全局设置表
CREATE TABLE IF NOT EXISTS `site_setting` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `intro_media_type` VARCHAR(50) DEFAULT 'image' COMMENT '开场媒体类型: video 或 image',
  `intro_media_url` VARCHAR(255) DEFAULT NULL COMMENT '开场媒体的访问链接',
  `site_name` VARCHAR(100) DEFAULT 'My Blog' COMMENT '网站名称',
  `site_signature` VARCHAR(255) DEFAULT 'Hello World' COMMENT '站长签名',
  `record_number` VARCHAR(100) DEFAULT NULL COMMENT 'ICP备案号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='网站全局设置表';

