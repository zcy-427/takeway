# Takeaway-V4: 企业级高并发外卖核心架构实战

本项目是一个基于 Spring Boot 3.x 构建的高性能外卖核心后端微服务。经历了多次架构演进，项目彻底告别了传统的单机同步 CRUD 模式，深度整合了 **Redis 缓存旁路**与 **RabbitMQ 消息队列**，构建了一套足以应对“双十一”级别高并发洪峰的“高吞吐、强一致、零超卖”的现代微服务架构。

## 🌟 核心架构亮点 (Core Architecture Highlights)

### 1. 🌪️ 读优化：Redis 缓存旁路模式 (Cache Aside)
- **百倍 QPS 提升**：针对外卖系统中“读多写少”的菜单浏览场景，引入 Redis 作为大堂级缓存。拦截 99% 的数据库穿透请求，实现内存级极速响应。
- **一致性保障**：配合 `ObjectMapper` 实现 JSON 序列化存储。提供 `/flush-cache` 缓存失效接口，在 CUD (增删改) 动作时主动撕毁旧缓存，保障 Cache 与 MySQL 之间的数据最终一致性。

### 2. 🚀 写优化：RabbitMQ 异步解耦与削峰填谷
- **彻底告别同步阻塞**：重构核心下单链路，Tomcat 网关（Controller）仅负责校验并投递 `OrderMessageDTO` 到 RabbitMQ，实现毫秒级“排队”响应。
- **后台平滑消费**：引入 `@RabbitListener` 作为后台大厨，根据服务器真实吞吐极限平滑拉取订单。彻底解决瞬时 10 万级洪峰流量导致的 Tomcat 线程爆满与 OOM 宕机危机。

### 3. 🛡️ 千万级并发防超卖体系 (绝对防御)
- **前置漏斗**：依托 `Redisson` 分布式锁，极速拦截无效洪峰，保障同一商品的单线程扣减环境。
- **中置包裹**：摒弃有“幻读”隐患的 `@Transactional` 注解，改用 `TransactionTemplate` 编程式事务。精准控制“拿锁 -> 开事务 -> 扣库 -> 提交事务 -> 释放锁”的绝对安全生命周期。
- **底层兜底**：手写 MyBatis 乐观锁级别安全 SQL (`stock = stock - 1 WHERE stock >= 1`)，构筑数据落盘的最后一道坚固防线。

### 4. 🔐 企业级零信任安全与 API 规范
- **密码学粉碎机**：集成 `jBcrypt` 实现密码的不可逆加盐哈希（Hash），从数学层面免疫数据库脱库造成的明文泄露。
- **无状态门禁**：基于 Auth0 JWT + 自定义 `HandlerInterceptor` 拦截器，实现全链路无状态鉴权与请求级 `UserId` 隐式透传，防范前端伪造越权。
- **优雅兜底**：基于 `@RestControllerAdvice` 构建全局异常处理器，统一封装标准 `Result<T>` 泛型响应体，提供极其丝滑的前后端对接体验。

## 🛠️ 技术栈 (Tech Stack)

- **核心框架**：Spring Boot 3.2.x
- **持久层架构**：MyBatis-Plus 3.5.7 + MySQL 8.0
- **中间件集群**：
  - **Redis + Redisson 3.27** (分布式锁 & 数据缓存)
  - **RabbitMQ (AMQP)** (异步解耦 & 消息队列)
- **安全与工具**：jBcrypt / Auth0 JWT / Jackson / Lombok

## 🚀 极速启动 (Quick Start)

### 1. 基础设施准备 (Docker 极速部署)
确保本机已安装 JDK 17+ 与 Maven 3.8+，并启动必要的基础设施：
```bash
# 启动 Redis
docker run -d --name my-redis -p 6379:6379 redis
# 启动 RabbitMQ (带可视化管理后台)
docker run -d --name my-rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
