# 校园书环 - 项目说明文档

<p align="left">
  <img src="https://img.shields.io/badge/SPRING%20BOOT-3.2.0-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" />
  <img src="https://img.shields.io/badge/REACT-18.2.0-20232A?style=for-the-badge&logo=react&logoColor=61DAFB" />
  <img src="https://img.shields.io/badge/JAVA-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/MYSQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white" />
  <img src="https://img.shields.io/badge/JWT-JSON%20WEB%20TOKEN-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" />
</p>

<p align="left">
  <img src="https://img.shields.io/badge/LICENSE-MIT-yellow?style=for-the-badge" />
  <img src="https://img.shields.io/badge/STATUS-COMPLETED-success?style=for-the-badge" />
</p>

## 📚 项目概述

**校园书环** (Campus Book Loop) 是一个校园二手书交易平台，旨在为大学生提供便捷的二手教材买卖服务。平台支持书籍发布、在线交易、实时聊天、求购发布等功能。

### 核心特性
- 📖 **书籍交易**: 发布、浏览、购买二手教材
- 💬 **即时通讯**: 买卖双方在线沟通
- 🔍 **智能搜索**: 按分类、关键词快速查找
- 📢 **求购发布**: 发布求购信息，快速找到所需书籍
- ⭐ **收藏功能**: 收藏感兴趣的书籍
- 🎓 **学生认证**: 学生身份验证机制
- 📊 **后台管理**: 完善的管理员后台系统
- 📱 **文件上传**: 图片上传与管理功能

---

## 🛠️ 技术栈

### 后端 (Backend)
- **框架**: Spring Boot 3.2.0
- **数据库**: MySQL 8.0+
- **ORM**: Spring Data JPA (Hibernate)
- **安全**: Spring Security + JWT Token 认证 (jjwt 0.12.3)
- **模板引擎**: Thymeleaf (管理后台)
- **数据校验**: Spring Boot Validation
- **开发工具**: Lombok 1.18.34, Spring Boot DevTools
- **构建工具**: Maven
- **Java 版本**: JDK 21

### 前端 (MiniProgram)
- **平台**: 微信小程序
- **语言**: JavaScript, WXML, WXSS
- **API 通信**: wx.request (RESTful)
- **特性**: 自定义 TabBar, 懒加载 (lazyCodeLoading)

### 开发工具
- **后端 IDE**: IntelliJ IDEA / Eclipse
- **小程序开发**: 微信开发者工具
- **数据库工具**: MySQL Workbench / Navicat
- **版本控制**: Git

---

## 📁 项目结构

```
campus-book-loop/
├── backend/                          # 后端项目
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/campusbookloop/
│           │       ├── CampusBookLoopApplication.java  # 应用入口
│           │       ├── controller/   # 控制器层 (10个)
│           │       │   ├── AdminController.java        # 管理后台
│           │       │   ├── AuthController.java         # 认证相关
│           │       │   ├── BookController.java         # 书籍管理
│           │       │   ├── FavoriteController.java     # 收藏功能
│           │       │   ├── FileUploadController.java   # 文件上传
│           │       │   ├── MessageController.java      # 消息通讯
│           │       │   ├── OrderController.java        # 订单管理
│           │       │   ├── StaticFileController.java   # 静态资源
│           │       │   ├── UserController.java         # 用户管理
│           │       │   └── WantedPostController.java   # 求购管理
│           │       ├── service/      # 业务逻辑层 (6个)
│           │       ├── repository/   # 数据访问层 (7个)
│           │       ├── entity/       # 实体类 (7个)
│           │       │   ├── User.java
│           │       │   ├── Book.java
│           │       │   ├── Order.java
│           │       │   ├── Favorite.java
│           │       │   ├── WantedPost.java
│           │       │   ├── Conversation.java   # 会话
│           │       │   └── Message.java        # 消息
│           │       ├── dto/          # 数据传输对象 (14个)
│           │       ├── config/       # 配置类 (3个)
│           │       ├── exception/    # 异常处理 (1个)
│           │       └── util/         # 工具类 (1个)
│           └── resources/
│               ├── application.yml   # 应用配置
│               ├── static/           # 静态资源
│               │   └── css/
│               │       └── admin.css
│               └── templates/        # 管理后台模板 (10个)
│                   └── admin/
│                       ├── dashboard.html      # 仪表盘
│                       ├── users.html          # 用户列表
│                       ├── user-add.html       # 添加用户
│                       ├── user-edit.html      # 编辑用户
│                       ├── books.html          # 书籍列表
│                       ├── book-add.html       # 添加书籍
│                       ├── book-edit.html      # 编辑书籍
│                       ├── orders.html         # 订单列表
│                       ├── verifications.html  # 认证审核
│                       └── wanted.html         # 求购管理
│
└── miniprogram/                      # 小程序前端
    ├── pages/                        # 页面 (16个)
    │   ├── login/                    # 登录页面
    │   ├── mobile-login/             # 手机号登录
    │   ├── register/                 # 注册页面
    │   ├── home/                     # 首页
    │   ├── wanted/                   # 求购区
    │   ├── publish/                  # 发布页面
    │   ├── publish-edit/             # 编辑已发布书籍
    │   ├── publish-records/          # 发布记录
    │   ├── publish-wanted/           # 发布求购
    │   ├── message/                  # 消息列表
    │   ├── chat-detail/              # 聊天详情
    │   ├── me/                       # 个人中心
    │   ├── book-detail/              # 书籍详情
    │   ├── checkout/                 # 结算页面
    │   ├── orders/                   # 订单列表
    │   └── student-verification/     # 学生认证
    ├── custom-tab-bar/               # 自定义 TabBar
    ├── utils/                        # 工具函数
    │   └── api.js                    # API 封装
    ├── app.js                        # 应用入口
    ├── app.json                      # 应用配置
    └── app.wxss                      # 全局样式
```

---

## 💾 数据库设计

### 核心表结构

#### users (用户表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| open_id | VARCHAR(100) | 微信 OpenID |
| nickname | VARCHAR(50) | 昵称 |
| phone | VARCHAR(20) | 手机号 |
| avatar_url | VARCHAR(500) | 头像 URL |
| school | VARCHAR(100) | 学校 |
| student_id | VARCHAR(50) | 学号 |
| student_id_card_url | VARCHAR(500) | 学生证照片 |
| verification_status | TINYINT | 认证状态: 0-未认证, 1-待审核, 2-已认证, 3-认证失败 |
| role | TINYINT | 角色: 0-普通用户, 1-管理员 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

#### books (书籍表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| title | VARCHAR(200) | 书名 |
| author | VARCHAR(100) | 作者 |
| publisher | VARCHAR(100) | 出版社 |
| isbn | VARCHAR(50) | ISBN |
| original_price | DECIMAL(10,2) | 原价 |
| price | DECIMAL(10,2) | 售价 |
| book_condition | VARCHAR(50) | 成色 |
| category | VARCHAR(50) | 分类 |
| description | TEXT | 描述 |
| images | JSON | 图片列表 |
| seller_id | BIGINT | 卖家 ID (外键) |
| status | TINYINT | 状态: 0-在售, 1-已预订, 2-已售出, 3-已下架 |
| view_count | INT | 浏览量 |
| favorite_count | INT | 收藏量 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

#### orders (订单表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| order_no | VARCHAR(50) | 订单号 |
| book_id | BIGINT | 书籍 ID (外键) |
| buyer_id | BIGINT | 买家 ID (外键) |
| seller_id | BIGINT | 卖家 ID (外键) |
| price | DECIMAL(10,2) | 成交价格 |
| address | VARCHAR(200) | 交易地址 |
| remark | VARCHAR(500) | 备注 |
| status | TINYINT | 状态: 0-待付款, 1-待发货, 2-待收货, 3-已完成, 4-已取消 |
| payment_method | TINYINT | 支付方式: 0-线下, 1-微信 |
| pay_time | DATETIME | 支付时间 |
| delivery_time | DATETIME | 发货时间 |
| receive_time | DATETIME | 收货时间 |
| cancel_time | DATETIME | 取消时间 |
| cancel_reason | VARCHAR(200) | 取消原因 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

#### favorites (收藏表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户 ID (外键) |
| book_id | BIGINT | 书籍 ID (外键) |
| created_at | DATETIME | 创建时间 |

#### wanted_posts (求购表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 用户 ID (外键) |
| title | VARCHAR(200) | 标题 |
| author | VARCHAR(100) | 作者 |
| category | VARCHAR(50) | 分类 |
| max_price | DECIMAL(10,2) | 最高出价 |
| description | TEXT | 描述 |
| status | TINYINT | 状态: 0-求购中, 1-已完成, 2-已取消 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

#### conversations (会话表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user1_id | BIGINT | 用户1 ID (外键) |
| user2_id | BIGINT | 用户2 ID (外键) |
| last_message | TEXT | 最后一条消息 |
| last_message_time | DATETIME | 最后消息时间 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

#### messages (消息表)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| conversation_id | BIGINT | 会话 ID (外键) |
| sender_id | BIGINT | 发送者 ID (外键) |
| content | TEXT | 消息内容 |
| is_read | BOOLEAN | 是否已读 |
| created_at | DATETIME | 创建时间 |

---

## 🔌 API 接口文档

### 基础信息
- **Base URL**: `http://localhost:8080/api`
- **认证方式**: JWT Token (Header: `Authorization: Bearer <token>`)
- **响应格式**: JSON

### 1. 用户相关

#### 用户注册
```
POST /auth/register
Body: {
  "nickname": "张三",
  "phone": "13800138000",
  "password": "123456",
  "code": "1234"  // 验证码
}
Response: {
  "code": 0,
  "message": "注册成功",
  "data": {
    "token": "eyJhbGc...",
    "userId": 1
  }
}
```

#### 用户登录
```
POST /auth/login
Body: {
  "phone": "13800138000",
  "password": "123456"
}
Response: {
  "code": 0,
  "data": {
    "token": "eyJhbGc...",
    "userInfo": { ... }
  }
}
```

#### 获取用户信息
```
GET /users/me
Headers: Authorization: Bearer <token>
Response: {
  "code": 0,
  "data": {
    "id": 1,
    "nickname": "张三",
    "listedCount": 5,
    "soldCount": 2,
    "boughtCount": 3
  }
}
```

### 2. 书籍相关

#### 获取书籍列表
```
GET /books?page=0&size=20&category=计算机&keyword=算法
Response: {
  "code": 0,
  "data": {
    "content": [...],
    "totalElements": 100,
    "totalPages": 5
  }
}
```

#### 获取书籍详情
```
GET /books/{bookId}
Response: {
  "code": 0,
  "data": {
    "id": 1,
    "title": "算法导论",
    "price": 45.00,
    ...
  }
}
```

#### 发布书籍
```
POST /books
Headers: Authorization: Bearer <token>
Body: {
  "title": "算法导论",
  "author": "Cormen",
  "price": 45.00,
  "originalPrice": 128.00,
  "condition": "九成新",
  "category": "计算机",
  "description": "...",
  "images": ["url1", "url2"]
}
```

#### 更新书籍
```
PUT /books/{bookId}
Headers: Authorization: Bearer <token>
Body: { ... }
```

#### 删除书籍 (软删除)
```
DELETE /books/{bookId}
Headers: Authorization: Bearer <token>
Response: {
  "code": 0,
  "message": "删除成功"
}
```

### 3. 订单相关

#### 创建订单
```
POST /orders
Headers: Authorization: Bearer <token>
Body: {
  "bookId": 1,
  "address": "东区宿舍楼下",
  "remark": "下午3点",
  "paymentMethod": 0
}
```

#### 支付订单
```
POST /orders/{orderId}/pay
Headers: Authorization: Bearer <token>
```

#### 发货
```
POST /orders/{orderId}/deliver
Headers: Authorization: Bearer <token>
```

#### 确认收货
```
POST /orders/{orderId}/receive
Headers: Authorization: Bearer <token>
```

#### 获取买家订单
```
GET /orders/buy?status=0
Headers: Authorization: Bearer <token>
```

#### 获取卖家订单
```
GET /orders/sell?status=1
Headers: Authorization: Bearer <token>
```

### 4. 收藏相关

#### 收藏书籍
```
POST /favorites/{bookId}
Headers: Authorization: Bearer <token>
```

#### 取消收藏
```
DELETE /favorites/{bookId}
Headers: Authorization: Bearer <token>
```

#### 获取收藏列表
```
GET /favorites?page=0&size=20
Headers: Authorization: Bearer <token>
```

### 5. 求购相关

#### 发布求购
```
POST /wanted
Headers: Authorization: Bearer <token>
Body: {
  "title": "离散数学",
  "author": "...",
  "category": "计算机",
  "maxPrice": 40.00,
  "description": "..."
}
```

#### 获取求购列表
```
GET /wanted?page=0&size=20&status=0
```

### 6. 消息相关

#### 发送消息
```
POST /messages
Headers: Authorization: Bearer <token>
Body: {
  "receiverId": 2,
  "content": "你好，这本书还在吗？"
}
```

#### 获取会话列表
```
GET /messages/conversations
Headers: Authorization: Bearer <token>
```

#### 获取聊天记录
```
GET /messages/conversation/{conversationId}
Headers: Authorization: Bearer <token>
```

### 7. 文件上传

#### 上传图片
```
POST /upload/image
Headers: Authorization: Bearer <token>
Content-Type: multipart/form-data
Body: file (图片文件)
Response: {
  "code": 0,
  "data": {
    "url": "https://..."
  }
}
```

---

## 🚀 部署指南

### 后端部署

#### 1. 环境准备
```bash
# 安装 Java 21
java -version

# 安装 MySQL 8.0+
mysql --version
```

#### 2. 数据库初始化
```sql
CREATE DATABASE campus_book_loop 
  CHARACTER SET utf8mb4 
  COLLATE utf8mb4_unicode_ci;
```

#### 3. 配置文件
修改 `backend/src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/campus_book_loop
    username: your_username
    password: your_password
```

#### 4. 构建运行
```bash
cd backend
mvn clean package
java -jar target/campus-book-loop-backend-1.0.0.jar
```

### 前端部署

#### 1. 导入项目
- 打开微信开发者工具
- 导入 `miniprogram` 目录

#### 2. 配置 AppID
修改 `project.config.json`:
```json
{
  "appid": "your_appid"
}
```

#### 3. 配置 API 地址
修改 `miniprogram/utils/api.js`:
```javascript
const BASE_URL = 'https://your-domain.com/api';
```

#### 4. 上传发布
- 点击 "上传" 按钮
- 在微信公众平台提交审核

---

## 📖 使用说明

### 普通用户

1. **注册登录**
   - 使用手机号注册
   - 或使用微信一键登录

2. **浏览书籍**
   - 首页浏览最新书籍
   - 使用搜索功能查找
   - 按分类筛选

3. **购买书籍**
   - 查看书籍详情
   - 点击"立即购买"
   - 填写交易地址
   - 确认支付
   - 等待发货
   - 确认收货

4. **发布书籍**
   - 点击"发布"按钮
   - 填写书籍信息
   - 上传图片
   - 提交发布
   - 可在"发布记录"中编辑已发布书籍

5. **求购书籍**
   - 进入求购区
   - 点击"发布求购"
   - 填写求购信息
   - 等待回复

6. **即时通讯**
   - 在书籍详情页联系卖家
   - 在消息页面查看所有会话
   - 实时聊天沟通

### 管理员

访问 `http://localhost:8080/admin` 进入后台管理系统。

**功能包括**:
- 📊 数据概览 (含订单趋势图表)
- 👥 用户管理 (列表/添加/编辑)
- 📖 书籍管理 (列表/添加/编辑)
- 📦 订单管理
- ✅ 认证审核
- 📢 求购管理

---

## 🔧 开发指南

### 添加新功能

#### 后端
1. 在 `entity` 包中创建实体类
2. 在 `repository` 包中创建 Repository 接口
3. 在 `service` 包中实现业务逻辑
4. 在 `controller` 包中创建 API 接口
5. 如需管理后台，在 `templates/admin` 中添加模板

#### 前端
1. 在 `pages` 目录创建新页面
2. 在 `app.json` 中注册页面
3. 使用 `utils/api.js` 调用后端接口

### 调试技巧

**后端**:
```bash
# 查看日志
tail -f logs/spring.log

# 开启 SQL 日志
spring.jpa.show-sql=true
```

**前端**:
```javascript
// 控制台调试
console.log('调试信息', data);

// 使用调试工具
wx.setEnableDebug({ enableDebug: true });
```

---

## 📝 注意事项

1. **安全性**
   - 生产环境务必修改 JWT Secret
   - 使用 HTTPS 协议
   - 定期备份数据库

2. **性能优化**
   - 适当使用数据库索引
   - 启用 Redis 缓存
   - 图片使用 CDN

3. **微信小程序限制**
   - 需配置合法域名
   - 请求必须使用 HTTPS
   - 注意包大小限制 (2MB)

---

## 📞 技术支持

如有问题，请联系开发团队或提交 Issue。

---

**最后更新时间**: 2025-12-21  
**版本号**: v1.0.0
