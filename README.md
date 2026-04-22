# Takeaway-V2: 企业级高并发外卖交易与鉴权系统

本项目是一个基于 Spring Boot 3.x 构建的高性能外卖核心后端微服务。项目不仅完成了标准化的交易链路与 JWT 安全鉴权，更重点针对**高并发抢购（秒杀）场景**进行了深度架构演进，完美解决了多线程环境下的“超卖”灾难，并实现了企业级的全局异常接管与 API 规范。

## 🌟 核心架构亮点 (Core Highlights)

- ⚡ **千万级并发防超卖体系（核心）**：
  构建了坚不可摧的三道防线：
  1. **前置漏斗**：引入 `Redisson` 分布式锁，极速拦截无效洪峰流量。
  2. **中置包裹**：弃用具有“幻读”隐患的 `@Transactional` 注解，改用 `TransactionTemplate` 编程式事务。精准控制“拿锁 -> 开事务 -> 扣库存 -> 提交事务 -> 释放锁”的绝对执行顺序，彻底根除高并发下的事务提交延迟问题。
  3. **底层兜底**：手写 MyBatis 乐观锁安全扣减 SQL (`stock = stock - 1 WHERE stock >= 1`)，实现数据库级别的绝对防御。

- 📦 **企业级 API 规范与全局兜底**：
  弃用原生的 500 报错页面，引入 `@RestControllerAdvice` 构建全局异常处理器（GlobalExceptionHandler）。统一步装标准 `Result<T>` 响应体，将所有业务异常转化为优雅的 JSON 返回，提供极其丝滑的前后端对接体验。

- 🛡️ **无状态 JWT 鉴权闭环**：
  手写全局请求拦截器（Interceptor），配合 Auth0 JWT 实现无状态登录。通过 `HttpServletRequest` 隐式传递 `currentUserId` 贯穿整个请求链路，彻底封杀了前端伪造 JSON ID 的“越权下单”漏洞。

- 🔐 **密码学级别安全护城河**：
  告别明文存储，集成 `jBcrypt` 算法对用户密码进行不可逆的加盐哈希（Hash）处理。从数学层面确保即使发生极端的数据库泄露（脱库），用户的真实密码依然绝对安全。

- 🧱 **DTO/VO 领域防腐与视图隔离**：
  严格推行数据传输对象（DTO）与视图对象（VO）架构。精准控制入参边界，拦截多余的非法参数；并在出参时严格脱敏内部隐私数据（如主键 ID、历史快照价格等）。

## 🛠️ 技术栈 (Tech Stack)

- **核心框架**：Spring Boot 3.2.x
- **持久层**：MyBatis-Plus + MySQL 8.0
- **高并发与锁**：Redis + Redisson 3.27.x
- **安全体系**：jBcrypt (密码哈希) / Auth0 JWT (无状态令牌)
- **工程化**：Lombok / Maven / Postman (并发压测)

## 🚀 快速启动 (Quick Start)

### 1. 环境准备
确保本机已安装 JDK 17+, MySQL 8.0, Maven 3.8+，以及 **Redis** (推荐使用 Docker 极速部署：`docker run -d -p 6379:6379 redis`)。

### 2. 数据库初始化
在 MySQL 中创建数据库 `takeway_db`，并运行相关 SQL 脚本创建 `user`, `product`, `orders`, `order_item` 等表，并插入测试菜品数据。

### 3. 修改配置
请检查 `src/main/resources/application.yaml`，确保数据库与 Redis 密码及端口正确：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/takeway_db?...
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
```
