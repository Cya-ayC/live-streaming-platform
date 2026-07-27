-- Nacos 数据库初始化
CREATE DATABASE IF NOT EXISTS `nacos` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `nacos`;

-- 直播平台数据库初始化
CREATE DATABASE IF NOT EXISTS `live_platform` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `live_platform`;

-- 用户表
CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `email` VARCHAR(100) UNIQUE COMMENT '邮箱',
  `phone` VARCHAR(20) UNIQUE COMMENT '电话号码',
  `nick_name` VARCHAR(50) COMMENT '昵称',
  `avatar_url` VARCHAR(500) COMMENT '头像URL',
  `bio` VARCHAR(500) COMMENT '个人简介',
  `gender` TINYINT COMMENT '性别: 0-保密, 1-男, 2-女',
  `follow_count` INT DEFAULT 0 COMMENT '关注数',
  `follower_count` INT DEFAULT 0 COMMENT '粉丝数',
  `account_balance` DECIMAL(10, 2) DEFAULT 0 COMMENT '账户余额',
  `account_status` TINYINT DEFAULT 1 COMMENT '账户状态: 0-禁用, 1-正常',
  `last_login_time` TIMESTAMP COMMENT '最后登录时间',
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` BOOLEAN DEFAULT FALSE COMMENT '是否删除',
  KEY `idx_username` (`username`),
  KEY `idx_email` (`email`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 直播间表
CREATE TABLE IF NOT EXISTS `live_rooms` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '直播间ID',
  `room_id` VARCHAR(50) UNIQUE NOT NULL COMMENT '直播间唯一标识',
  `anchor_id` BIGINT NOT NULL COMMENT '主播ID',
  `title` VARCHAR(200) NOT NULL COMMENT '直播间标题',
  `description` TEXT COMMENT '直播间描述',
  `cover_url` VARCHAR(500) COMMENT '直播间封面',
  `category_id` BIGINT COMMENT '分类ID',
  `status` ENUM('PREPARING', 'LIVE', 'FINISHED') DEFAULT 'PREPARING' COMMENT '直播状态',
  `viewer_count` INT DEFAULT 0 COMMENT '观看人数',
  `like_count` BIGINT DEFAULT 0 COMMENT '点赞数',
  `share_count` BIGINT DEFAULT 0 COMMENT '分享数',
  `start_time` TIMESTAMP COMMENT '开播时间',
  `end_time` TIMESTAMP COMMENT '下播时间',
  `push_url` VARCHAR(500) COMMENT '推流地址',
  `pull_url` VARCHAR(500) COMMENT '拉流地址',
  `is_public` BOOLEAN DEFAULT TRUE COMMENT '是否公开',
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` BOOLEAN DEFAULT FALSE COMMENT '是否删除',
  FOREIGN KEY (`anchor_id`) REFERENCES `users`(`id`),
  KEY `idx_anchor_id` (`anchor_id`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_status` (`status`),
  KEY `idx_start_time` (`start_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='直播间表';

-- 用户关注表
CREATE TABLE IF NOT EXISTS `user_follow` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关注ID',
  `follower_id` BIGINT NOT NULL COMMENT '粉丝ID',
  `following_id` BIGINT NOT NULL COMMENT '被关注者ID',
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY `uk_follower_following` (`follower_id`, `following_id`),
  FOREIGN KEY (`follower_id`) REFERENCES `users`(`id`),
  FOREIGN KEY (`following_id`) REFERENCES `users`(`id`),
  KEY `idx_following_id` (`following_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户关注表';

-- 评论表
CREATE TABLE IF NOT EXISTS `comments` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
  `room_id` BIGINT NOT NULL COMMENT '直播间ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `content` VARCHAR(500) NOT NULL COMMENT '评论内容',
  `parent_id` BIGINT COMMENT '父评论ID',
  `like_count` BIGINT DEFAULT 0 COMMENT '点赞数',
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` BOOLEAN DEFAULT FALSE COMMENT '是否删除',
  FOREIGN KEY (`room_id`) REFERENCES `live_rooms`(`id`),
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 订单表
CREATE TABLE IF NOT EXISTS `orders` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
  `order_id` VARCHAR(50) UNIQUE NOT NULL COMMENT '订单号',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `product_id` BIGINT NOT NULL COMMENT '商品ID',
  `product_name` VARCHAR(200) COMMENT '商品名称',
  `quantity` INT DEFAULT 1 COMMENT '数量',
  `price` DECIMAL(10, 2) NOT NULL COMMENT '单价',
  `total_amount` DECIMAL(10, 2) NOT NULL COMMENT '总金额',
  `status` ENUM('PENDING', 'PAID', 'DELIVERING', 'COMPLETED', 'CANCELLED', 'REFUNDED') DEFAULT 'PENDING' COMMENT '订单状态',
  `payment_method` VARCHAR(50) COMMENT '支付方式',
  `payment_time` TIMESTAMP COMMENT '支付时间',
  `remark` VARCHAR(500) COMMENT '备注',
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` BOOLEAN DEFAULT FALSE COMMENT '是否删除',
  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 创建索引供统计查询使用
CREATE TABLE IF NOT EXISTS `statistics` (
  `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '统计ID',
  `stat_date` DATE NOT NULL COMMENT '统计日期',
  `stat_type` VARCHAR(50) COMMENT '统计类型',
  `stat_key` VARCHAR(100) COMMENT '统计键',
  `stat_value` BIGINT DEFAULT 0 COMMENT '统计值',
  `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY `uk_date_type_key` (`stat_date`, `stat_type`, `stat_key`),
  KEY `idx_stat_date` (`stat_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='统计表';
