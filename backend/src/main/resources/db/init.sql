-- 校园书环数据库初始化脚本
-- Campus Book Loop Database Initialization Script

-- 创建数据库
CREATE DATABASE IF NOT EXISTS campus_book_loop 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE campus_book_loop;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    open_id VARCHAR(100) UNIQUE COMMENT '微信openId',
    nickname VARCHAR(50) COMMENT '用户昵称',
    avatar_url VARCHAR(255) COMMENT '头像URL',
    phone VARCHAR(20) UNIQUE COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    school VARCHAR(100) COMMENT '学校',
    student_id VARCHAR(50) COMMENT '学号',
    real_name VARCHAR(50) COMMENT '真实姓名',
    student_id_card_url VARCHAR(255) COMMENT '学生证照片URL',
    verification_status TINYINT DEFAULT 0 COMMENT '认证状态: 0-未认证 1-待审核 2-已认证 3-认证失败',
    role TINYINT DEFAULT 0 COMMENT '角色: 0-普通用户 1-管理员',
    status TINYINT DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_open_id (open_id),
    INDEX idx_phone (phone),
    INDEX idx_verification_status (verification_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 书籍表
CREATE TABLE IF NOT EXISTS books (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '书名',
    author VARCHAR(100) COMMENT '作者',
    publisher VARCHAR(100) COMMENT '出版社',
    isbn VARCHAR(50) COMMENT 'ISBN号',
    original_price DECIMAL(10,2) NOT NULL COMMENT '原价',
    price DECIMAL(10,2) NOT NULL COMMENT '售价',
    `condition` VARCHAR(50) COMMENT '成色: 全新/九成新/八成新/七成新',
    category VARCHAR(50) COMMENT '分类: 专业教材/考试辅导/文学小说/其他',
    description TEXT COMMENT '描述',
    images JSON COMMENT '图片URL列表',
    seller_id BIGINT NOT NULL COMMENT '卖家ID',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-在售 1-已预订 2-已售出 3-已下架',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    favorite_count INT DEFAULT 0 COMMENT '收藏量',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_seller_id (seller_id),
    INDEX idx_status (status),
    INDEX idx_category (category),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (seller_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='书籍表';

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    book_id BIGINT NOT NULL COMMENT '书籍ID',
    buyer_id BIGINT NOT NULL COMMENT '买家ID',
    seller_id BIGINT NOT NULL COMMENT '卖家ID',
    price DECIMAL(10,2) NOT NULL COMMENT '成交价格',
    address VARCHAR(200) COMMENT '交易地址',
    remark VARCHAR(500) COMMENT '备注',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-待付款 1-待发货 2-待收货 3-已完成 4-已取消',
    payment_method TINYINT DEFAULT 0 COMMENT '支付方式: 0-线下支付 1-微信支付',
    pay_time DATETIME COMMENT '支付时间',
    delivery_time DATETIME COMMENT '发货时间',
    receive_time DATETIME COMMENT '收货时间',
    cancel_time DATETIME COMMENT '取消时间',
    cancel_reason VARCHAR(200) COMMENT '取消原因',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_no (order_no),
    INDEX idx_book_id (book_id),
    INDEX idx_buyer_id (buyer_id),
    INDEX idx_seller_id (seller_id),
    INDEX idx_status (status),
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (buyer_id) REFERENCES users(id),
    FOREIGN KEY (seller_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- 消息表
CREATE TABLE IF NOT EXISTS messages (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sender_id BIGINT NOT NULL COMMENT '发送者ID',
    receiver_id BIGINT NOT NULL COMMENT '接收者ID',
    book_id BIGINT COMMENT '关联的书籍ID',
    content TEXT NOT NULL COMMENT '消息内容',
    type TINYINT DEFAULT 0 COMMENT '消息类型: 0-文本 1-图片 2-系统消息',
    is_read TINYINT DEFAULT 0 COMMENT '是否已读: 0-未读 1-已读',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    INDEX idx_sender_id (sender_id),
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_book_id (book_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息表';

-- 会话表
CREATE TABLE IF NOT EXISTS conversations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user1_id BIGINT NOT NULL COMMENT '用户1 ID',
    user2_id BIGINT NOT NULL COMMENT '用户2 ID',
    book_id BIGINT COMMENT '关联的书籍ID',
    last_message VARCHAR(500) COMMENT '最后一条消息内容',
    last_message_time DATETIME COMMENT '最后一条消息时间',
    unread_count1 INT DEFAULT 0 COMMENT '用户1未读消息数',
    unread_count2 INT DEFAULT 0 COMMENT '用户2未读消息数',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user1_id (user1_id),
    INDEX idx_user2_id (user2_id),
    INDEX idx_last_message_time (last_message_time),
    FOREIGN KEY (user1_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (user2_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='会话表';

-- 求购帖表
CREATE TABLE IF NOT EXISTS wanted_posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL COMMENT '标题/书名',
    author VARCHAR(100) COMMENT '作者',
    description TEXT COMMENT '详细描述',
    category VARCHAR(50) COMMENT '分类',
    max_price DECIMAL(10,2) COMMENT '期望最高价格',
    user_id BIGINT NOT NULL COMMENT '发布者ID',
    status TINYINT DEFAULT 0 COMMENT '状态: 0-求购中 1-已找到 2-已关闭',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_category (category),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='求购帖表';

-- 收藏表
CREATE TABLE IF NOT EXISTS favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    book_id BIGINT NOT NULL COMMENT '书籍ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY uk_user_book (user_id, book_id),
    INDEX idx_user_id (user_id),
    INDEX idx_book_id (book_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- 插入管理员用户
INSERT INTO users (nickname, phone, role, status, verification_status) 
VALUES ('管理员', '13800000000', 1, 1, 2);

-- 插入测试用户
INSERT INTO users (nickname, phone, role, status, verification_status, school, student_id, real_name) 
VALUES 
('张三', '13800000001', 0, 1, 2, '北京大学', '2020001', '张三'),
('李四', '13800000002', 0, 1, 2, '清华大学', '2020002', '李四'),
('王五', '13800000003', 0, 1, 0, '复旦大学', '2020003', '王五');

-- 插入测试书籍
INSERT INTO books (title, author, publisher, isbn, original_price, price, `condition`, category, description, seller_id, status, view_count, favorite_count)
VALUES 
('高等数学（第七版）上册', '同济大学数学系', '高等教育出版社', '9787040396638', 45.00, 25.00, '九成新', '专业教材', '无笔记无划痕，包邮', 2, 0, 120, 15),
('线性代数（第六版）', '同济大学数学系', '高等教育出版社', '9787040396645', 38.00, 20.00, '八成新', '专业教材', '有少量笔记', 2, 0, 85, 8),
('大学英语四级真题', '新东方教育', '西安交通大学出版社', '9787560590321', 58.00, 30.00, '全新', '考试辅导', '全新未拆封', 3, 0, 200, 25),
('三体（全三册）', '刘慈欣', '重庆出版社', '9787229042028', 88.00, 50.00, '九成新', '文学小说', '科幻巨作，品相完好', 3, 0, 300, 40);

-- 插入测试求购
INSERT INTO wanted_posts (title, author, description, category, max_price, user_id, status)
VALUES 
('求购《数据结构》', '严蔚敏', '求购严蔚敏版数据结构，C语言版', '专业教材', 25.00, 2, 0),
('求购考研英语真题', NULL, '需要近5年考研英语真题', '考试辅导', 40.00, 3, 0);
