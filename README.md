# 直播内容平台 - 企业级微服务项目

一个完整的、基于Spring Cloud微服务架构的企业级直播内容平台，包含用户系统、直播管理、内容分发、互动系统、推荐引擎、交易系统等完整功能模块。

## 📋 项目简介

本项目是一个从0开始构建的**企业级微服务系统**，涵盖直播平台的所有核心业务场景，旨在帮助开发者深入理解微服务架构、分布式系统设计、高并发优化等关键技术。

### 🎯 项目特点

- ✅ **完整的微服务架构**：9个独立微服务模块，完全解耦
- ✅ **分布式事务处理**：使用Seata实现强一致性事务
- ✅ **高可用设计**：Sentinel熔断限流、Gateway路由
- ✅ **消息驱动**：RabbitMQ异步解耦、事件驱动架构
- ✅ **分布式缓存**：Redis缓存策略、分布式锁
- ✅ **链路追踪**：Skywalking可观测性
- ✅ **容器化部署**：Docker Compose一键启动
- ✅ **生产就绪**：包含日志、监控、告警等运维能力

## 🏗️ 系统架构

```
┌─────────────────────────────────────────────────────────┐
│                     API 网关 (Gateway)                   │
│              Nacos + Sentinel + 负载均衡                │
└──────┬──────────────────────────────────────────────────┘
       │
       ├─────────────────────────────────────────────────────┐
       │                                                       │
   ┌───▼────────┐  ┌──────────────┐  ┌──────────────┐      │
   │ 用户服务    │  │  直播服务    │  │  内容服务    │      │
   │ User Service│  │ Live Service │  │Content Service     │
   └────────────┘  └──────────────┘  └──────────────┘      │
       │                                                       │
   ┌───▼────────┐  ┌──────────────┐  ┌──────────────┐      │
   │ 互动服务    │  │  推荐服务    │  │  订单服务    │      │
   │Interaction │  │Recommend Srv │  │Order Service │      │
   └────────────┘  └──────────────┘  └──────────────┘      │
       │                                                       │
   ┌───▼────────┐  ┌──────────────┐  ┌──────────────┐      │
   │ 支付服务    │  │  通知服务    │  │  统计服务    │      │
   │PaymentSrv  │  │NotifyService │  │Stats Service │      │
   └────────────┘  └──────────────┘  └──────────────┘      │
       │                                                       │
       └──────────────────────────┬──────────────────────────┘
                                  │
        ┌─────────────────────────┼─────────────────────────┐
        │                         │                         │
    ┌───▼───┐  ┌────────┐  ┌──────▼────┐  ┌────────────┐  │
    │ MySQL │  │ Redis  │  │ RabbitMQ  │  │  Nacos     │  │
    │ 8.0   │  │ Cache  │  │ Message   │  │ Registry   │  │
    └───────┘  └────────┘  └───────────┘  └────────────┘  │
        │                                                       │
        └───────────────────────────────────────────────────┘
```

## 📦 微服务模块说明

### 1. **网关服务 (Gateway)**
- 统一入口、路由转发
- Sentinel流量控制
- 请求认证授权
- 速率限流

### 2. **用户服务 (User Service)**
- 用户注册/登录
- 个人信息管理
- 粉丝关系管理
- 账户安全

### 3. **直播服务 (Live Service)**
- 直播间创建/管理
- 推流配置
- 直播状态管理
- 观众人数统计

### 4. **内容服务 (Content Service)**
- 视频上传
- 转码处理
- CDN分发
- 媒体库管理

### 5. **互动服务 (Interaction Service)**
- 评论系统
- 点赞功能
- 礼物打赏
- 弹幕系统

### 6. **推荐服务 (Recommend Service)**
- 个性化推荐
- 热榜排名
- 搜索功能
- 标签系统

### 7. **订单服务 (Order Service)**
- 礼物订单
- 充值订单
- 订单管理
- 分布式事务（Seata）

### 8. **支付服务 (Payment Service)**
- 第三方支付集成
- 支付流程管理
- 退款处理
- 对账系统

### 9. **通知服务 (Notify Service)**
- 消息推送
- 邮件通知
- 短信通知
- 站内信

### 10. **统计服务 (Statistics Service)**
- 数据分析
- BI报表
- 用户行为分析
- 平台运营数据

## 🛠️ 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 17 | 编程语言 |
| Spring Boot | 3.2.0 | 微服务框架 |
| Spring Cloud | 2023.0.0 | 微服务套件 |
| Spring Cloud Alibaba | 2022.0.0.0 | 分布式组件 |
| Nacos | 2.2.0 | 服务注册发现 + 配置中心 |
| Sentinel | 1.8.6 | 流量控制、熔断降级 |
| Spring Cloud Gateway | 4.0.0 | API网关 |
| Seata | 1.7.0 | 分布式事务 |
| RabbitMQ | 3.12 | 消息队列 |
| MySQL | 8.0 | 关系型数据库 |
| Redis | 7.0 | 缓存数据库 |
| Skywalking | 8.10.0 | 链路追踪 |
| Docker | Latest | 容器化 |
| Elasticsearch | 8.0 | 日志存储 |
| Kibana | 8.0 | 日志查询 |

## 🚀 快速开始

### 前置要求

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- Git

### 环境搭建

#### 1. 克隆项目

```bash
git clone https://github.com/Cya-ayC/live-streaming-platform.git
cd live-streaming-platform
```

#### 2. 启动基础设施（Docker Compose）

```bash
cd docker
docker-compose up -d
```

这将启动以下服务：
- MySQL 8.0
- Redis 7.0
- RabbitMQ 3.12
- Nacos 2.2.0
- Elasticsearch 8.0
- Kibana 8.0
- Skywalking 8.10.0

#### 3. 初始化数据库

```bash
# 进入MySQL容器
docker exec -it live-mysql mysql -uroot -p123456

# 执行SQL初始化脚本
source /docker-entrypoint-initdb.d/init.sql
```

#### 4. 编译项目

```bash
mvn clean package -DskipTests
```

#### 5. 启动各微服务

```bash
# 方式一：IDE中分别运行各服务的 *Application.java

# 方式二：命令行启动
cd gateway && mvn spring-boot:run
cd user-service && mvn spring-boot:run
cd live-service && mvn spring-boot:run
# ... 其他服务类似
```

#### 6. 验证服务

```bash
# 访问 Nacos 控制台
http://localhost:8848/nacos
# 用户名/密码: nacos/nacos

# 访问 Skywalking UI
http://localhost:8080

# 访问 RabbitMQ 管理界面
http://localhost:15672
# 用户名/密码: guest/guest

# 访问 Kibana
http://localhost:5601
```

#### 7. 测试API

```bash
# 通过Gateway访问服务
curl http://localhost:8080/api/user/login
```

## 📊 核心业务流程

### 用户开播流程

```
1. 用户创建直播间
   ↓
2. Live Service 返回推流地址
   ↓
3. 用户获取推流密钥（支付服务验证权限）
   ↓
4. 用户开始推流 → Content Service 转码
   ↓
5. 通知服务推送粉丝（异步）
   ↓
6. 观众开始观看 → 互动服务记录
   ↓
7. 直播结束 → 生成数据报表（统计服务）
```

### 观众打赏流程（分布式事务）

```
1. 观众选择礼物 → Order Service 创建订单
   ↓
2. Payment Service 调用第三方支付
   ↓
3. 支付成功 → Seata 协调事务：
   - Order Service: 订单状态更新
   - User Service: 主播余额增加
   - Interaction Service: 记录礼物赠送
   ↓
4. 通知服务发送消息给主播
   ↓
5. 异步更新统计数据
```

## 🔐 数据库设计

### 用户表 (users)
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nick_name VARCHAR(50),
    avatar_url VARCHAR(500),
    follow_count INT DEFAULT 0,
    follower_count INT DEFAULT 0,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

### 直播间表 (live_rooms)
```sql
CREATE TABLE live_rooms (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id VARCHAR(50) UNIQUE NOT NULL,
    anchor_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    cover_url VARCHAR(500),
    status ENUM('PREPARING', 'LIVE', 'FINISHED') DEFAULT 'PREPARING',
    viewer_count INT DEFAULT 0,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (anchor_id) REFERENCES users(id),
    INDEX idx_anchor_id (anchor_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

更多表结构见 `docs/database-schema.md`

## 💾 配置文件

### application.yml 示例

```yaml
spring:
  application:
    name: user-service
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: production
      config:
        server-addr: localhost:8848
        file-extension: yml
        namespace: production
    sentinel:
      transport:
        dashboard: localhost:8858
        port: 8719
  datasource:
    url: jdbc:mysql://localhost:3306/live_platform?useSSL=false&serverTimezone=UTC&characterEncoding=utf8mb4
    username: root
    password: 123456
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate

server:
  port: 8081
  servlet:
    context-path: /api
```

## 📝 API 文档

### Gateway 路由配置

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/api/user/**
          filters:
            - StripPrefix=1
        - id: live-service
          uri: lb://live-service
          predicates:
            - Path=/api/live/**
          filters:
            - StripPrefix=1
        # ... 其他服务
```

### 用户认证 API

```
POST /api/user/register
POST /api/user/login
GET /api/user/profile
PUT /api/user/profile
POST /api/user/follow/{userId}
GET /api/user/followers
```

### 直播管理 API

```
POST /api/live/room/create
GET /api/live/room/{roomId}
PUT /api/live/room/{roomId}
DELETE /api/live/room/{roomId}
POST /api/live/room/{roomId}/start
POST /api/live/room/{roomId}/end
GET /api/live/room/list
```

更多API见 `docs/api-documentation.md`

## 🧪 测试

### 单元测试

```bash
mvn test
```

### 集成测试

```bash
mvn verify
```

### 压力测试

```bash
# 使用 JMeter 或 Locust
jmeter -n -t tests/load-test.jmx
```

## 📚 详细文档

- [快速开始指南](docs/getting-started.md)
- [系统架构设计](docs/architecture.md)
- [数据库设计](docs/database-schema.md)
- [API文档](docs/api-documentation.md)
- [部署指南](docs/deployment.md)
- [监控告警](docs/monitoring.md)
- [常见问题](docs/faq.md)

## 🤝 贡献指南

欢迎提交PR！请遵循以下流程：

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📜 许可证

MIT License - 详见 [LICENSE](LICENSE) 文件

## 👨‍💻 作者

- **Cya-ayC** - 项目初始化和架构设计

## 📞 联系方式

- GitHub Issues: [Report Bug](https://github.com/Cya-ayC/live-streaming-platform/issues)
- 讨论区: [Discussions](https://github.com/Cya-ayC/live-streaming-platform/discussions)

## 🎓 学习资源

- [Spring Cloud 官方文档](https://spring.io/projects/spring-cloud)
- [Nacos 文档](https://nacos.io/)
- [Seata 分布式事务](https://seata.io/)
- [Sentinel 流量控制](https://sentinelguard.io/)

---

**⭐ 如果这个项目对您有帮助，请给一个Star！**
