# 系统架构设计文档

## 1. 总体架构

### 微服务拓扑图

```
┌─────────────────────────────────────────┐
│         客户端 (App/Web/H5)            │
└──────────────────┬──────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────┐
│   API Gateway (Spring Cloud Gateway)    │
│  - 请求路由                             │
│  - 流量控制 (Sentinel)                  │
│  - 认证授权                             │
│  - 速率限流                             │
└──────────────────┬──────────────────────┘
                   │
      ┌────────────┼────────────┐
      │            │            │
      ▼            ▼            ▼
   用户服务     直播服务      内容服务
      │            │            │
      └────────────┼────────────┘
                   │
      ┌────────────┼────────────┐
      │            │            │
      ▼            ▼            ▼
  互动服务      推荐服务      订单服务
      │            │            │
      └────────────┼────────────┘
                   │
      ┌────────────┼────────────┐
      │            │            │
      ▼            ▼            ▼
  支付服务      通知服务      统计服务
```

## 2. 核心组件

### 2.1 API 网关 (Gateway)

**职责：**
- 统一入口
- 路由转发
- 认证授权
- 流量控制
- 请求/响应转换

**配置示例：**
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
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20
```

### 2.2 服务注册发现 (Nacos)

**特点：**
- 支持CP和AP两种模式切换
- 服务自动注册/注销
- 健康检查
- 权重配置

**应用场景：**
- 服务动态发现
- 负载均衡
- 容错转移

### 2.3 流量控制 (Sentinel)

**保护规则：**

| 规则类型 | 说明 | 应用场景 |
|---------|------|----------|
| 流量控制 | 限制QPS | 高并发秒杀 |
| 熔断降级 | 故障隔离 | 依赖服务故障 |
| 热点参数 | 针对性限流 | 热门主播直播间 |
| 系统规则 | 整体保护 | 系统过载 |

**示例配置：**
```java
@Service
public class SentinelConfigService {
    @PostConstruct
    public void initSentinelRules() {
        // 流量控制规则
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule();
        rule.setResource("createLiveRoom");
        rule.setGrade(RuleConstants.FLOW_GRADE_QPS);
        rule.setCount(100);
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }
}
```

### 2.4 分布式事务 (Seata)

**事务模式对比：**

| 模式 | 一致性 | 隔离性 | 性能 | 应用场景 |
|------|-------|-------|------|----------|
| AT | 强一致性 | 读未提交 | 高 | 业务规则简单 |
| TCC | 强一致性 | 串行化 | 低 | 业务规则复杂 |
| Saga | 最终一致性 | 读已提交 | 很高 | 长事务、异步 |

**示例流程（打赏）：**
```
Order Service    Payment Service    User Service    Interaction Service
    │                   │                │                    │
    ├─ Begin ─ Seata Tx ───────────────────────────────────────┤
    │                                                            │
    ├─ Create Order ───────────────────────────────────────────►
    │                                                            │
    ├─ Call Payment ────────────────┐                          │
    │                               ▼                          │
    │                      Process Payment                      │
    │                               │                          │
    │                    Payment Success                       │
    │                               │                          │
    ├─ Update Balance ──────────────────────────────────────►  │
    │                                                            │
    ├─ Record Gift ─────────────────────────────────────────►  │
    │                                                            │
    └─ Commit ─ Seata Tx ──────────────────────────────────────┘
```

### 2.5 消息队列 (RabbitMQ)

**核心交换机和队列：**

```
交换机类型: Topic Exchange

直播事件交换机: live.events.ex
├─ live.room.created (新直播间创建)
├─ live.room.started (直播开始)
├─ live.room.ended (直播结束)
└─ live.viewer.joined (观众加入)

用户事件交换机: user.events.ex
├─ user.registered (用户注册)
├─ user.followed (关注)
└─ user.balance.updated (余额更新)

订单事件交换机: order.events.ex
├─ order.created (订单创建)
├─ order.paid (订单支付)
└─ order.refunded (订单退款)
```

**消费者配置示例：**
```java
@Configuration
public class RabbitMQConfig {
    // 直播事件
    @Bean
    public Queue liveRoomCreatedQueue() {
        return new Queue("live.room.created.queue");
    }

    @Bean
    public Binding liveRoomCreatedBinding(Queue liveRoomCreatedQueue) {
        return BindingBuilder.bind(liveRoomCreatedQueue)
            .to(topicExchange())
            .with("live.room.created");
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange("live.events.ex");
    }
}
```

### 2.6 缓存策略 (Redis)

**缓存分层：**

```
┌─────────────────────────────────────┐
│        应用层 (内存缓存)            │
│   ├─ Caffeine 本地缓存             │
│   └─ 热点数据 (1-5秒过期)          │
└──────────────────┬──────────────────┘
                   │
┌──────────────────▼──────────────────┐
│      Redis 分布式缓存               │
│   ├─ 用户信息 (1小时)              │
│   ├─ 直播间列表 (5分钟)            │
│   ├─ 推荐内容 (30分钟)             │
│   └─ 分布式锁                       │
└──────────────────┬──────────────────┘
                   │
┌──────────────────▼──────────────────┐
│      数据库 (MySQL)                 │
│   ├─ 完整数据存储                   │
│   ├─ 事务保证                       │
│   └─ 持久化                         │
└─────────────────────────────────────┘
```

**热点数据缓存示例：**
```java
@Service
public class LiveRoomService {
    private final RedisTemplate<String, Object> redisTemplate;
    
    public LiveRoom getLiveRoom(String roomId) {
        String cacheKey = "live:room:" + roomId;
        
        // 先查缓存
        LiveRoom cached = (LiveRoom) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        // 再查数据库
        LiveRoom room = liveRoomRepository.findByRoomId(roomId);
        if (room != null) {
            // 缓存30分钟
            redisTemplate.opsForValue().set(cacheKey, room, Duration.ofMinutes(30));
        }
        
        return room;
    }
}
```

## 3. 数据一致性保证

### 3.1 本地事务
- 单个数据库内的ACID操作
- 适用于单服务场景

### 3.2 分布式事务（Seata）
- AT模式：自动代理，强一致性
- TCC模式：业务代码实现，强一致性
- Saga模式：异步转账，最终一致性

### 3.3 最终一致性
- 消息队列异步处理
- 定时任务补偿
- 重试机制

## 4. 高可用设计

### 4.1 服务容错

```java
@Service
public class UserServiceClient {
    private final RestTemplate restTemplate;
    
    @CircuitBreaker(name = "userService", fallbackMethod = "getUserFallback")
    @Retry(name = "userService")
    @Bulkhead(name = "userService")
    public User getUser(Long userId) {
        return restTemplate.getForObject(
            "http://user-service/api/user/{id}", 
            User.class, 
            userId
        );
    }
    
    public User getUserFallback(Long userId, Exception ex) {
        log.warn("User service fallback for userId: {}", userId);
        return User.builder()
            .id(userId)
            .name("Unknown")
            .build();
    }
}
```

### 4.2 链路追踪
- Skywalking 自动化采集
- TraceId 贯穿全链路
- 性能监控和故障诊断

### 4.3 监控告警
- Prometheus 指标采集
- Grafana 可视化
- AlertManager 告警

## 5. 安全设计

### 5.1 认证
- JWT Token
- 令牌刷新机制
- 单点登录（SSO）

### 5.2 授权
- 基于角色的访问控制（RBAC）
- 资源级权限
- 动态权限管理

### 5.3 数据保护
- 敏感字段加密
- HTTPS 传输
- SQL注入防护

## 6. 性能优化

### 6.1 缓存优化
- 多层缓存策略
- 缓存穿透、雪崩、击穿保护
- 缓存预热

### 6.2 数据库优化
- 索引设计
- 查询优化
- 分库分表

### 6.3 异步处理
- 消息队列异步转移
- 线程池优化
- 响应流式传输

## 7. 部署架构

```
┌─────────────────────────────────────────┐
│        Kubernetes 集群                  │
│                                         │
│  ┌─────────────────────────────────┐  │
│  │   Ingress (Nginx)               │  │
│  └────────────────┬────────────────┘  │
│                   │                    │
│  ┌────────────────▼────────────────┐  │
│  │   Service Mesh (Istio)          │  │
│  │   - 流量管理                     │  │
│  │   - 安全策略                     │  │
│  │   - 可观测性                     │  │
│  └────────────────┬────────────────┘  │
│                   │                    │
│  ┌────────────────▼────────────────┐  │
│  │   Pod 副本集                     │  │
│  │   - 用户服务 (3副本)            │  │
│  │   - 直播服务 (3副本)            │  │
│  │   - 其他服务 (2副本)            │  │
│  └─────────────────────────────────┘  │
│                                         │
│  ┌─────────────────────────────────┐  │
│  │   存储层                        │  │
│  │   - 有状态服务 (StatefulSet)   │  │
│  │   - 持久化卷 (PVC)              │  │
│  └─────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

## 8. 扩展性考虑

### 8.1 水平扩展
- 无状态服务设计
- 负载均衡
- 自动扩缩容

### 8.2 垂直扩展
- 数据库分库分表
- Redis 集群
- 消息队列集群

### 8.3 新功能集成
- 插件化架构
- 功能开关（Feature Flag）
- API 版本管理
