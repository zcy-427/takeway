# Takeaway-Pro: 企业级高吞吐量外卖核心交易架构

本项目是一个基于 **Spring Boot 3.3** 构建的高性能外卖后端系统。项目经历了从单体 CRUD 到分布式架构的深度演进，重点解决了**高并发下的数据一致性、数据库抗压能力以及服务的容器化快速部署**。

## 🏗️ 核心架构演进 (Key Innovations)

### 1. 🚀 读优化：Redis 旁路缓存 (Cache Aside)
* **缓存命中逻辑**：针对菜单浏览请求，实现“先查 Redis、未命中回查 MySQL、异步回写 Redis”的经典链路。
* **性能提升**：将高频菜单查询从磁盘 I/O 转化为内存读取，QPS 提升 100 倍以上，彻底解放 MySQL。
* **一致性保障**：提供专用的缓存失效接口 `/product/flush-cache`，确保菜品更新时缓存的实时同步。

### 2. 🌪️ 写优化：RabbitMQ 异步解耦
* **流量削峰**：下单请求不再同步等待数据库写入，而是转化为 `OrderMessageDTO` 投递至 RabbitMQ 队列。
* **解耦设计**：Controller 仅需 1ms 即可返回“排队中”，复杂的业务逻辑由 `OrderListener` 在后台平稳消费处理。

### 3. 🛡️ 交易安全：Redisson + 编程式事务 (零超卖)
* **分布式锁**：引入 `Redisson` 实现细粒度商品锁，彻底拦截无效并发。
* **编程式事务**：弃用 `@Transactional`，改用 `TransactionTemplate` 精准控制事务在锁释放前提交，从底层杜绝由于事务提交延迟导致的超卖幻读。
* **安全扣减**：配合 MyBatis 乐观锁 SQL `stock >= #{num}`，构建数据一致性的最后防线。

### 4. 🐳 容器化编排：Docker Compose 一键启停
* **全栈编排**：通过 `docker-compose.yml` 一键集成 **MySQL 8.0、Redis、RabbitMQ 3-Management** 及 Spring Boot 应用。
* **动态环境**：利用 Docker 环境变量动态覆盖 Spring 配置，实现同一套 Jar 包在不同环境的无缝迁移。

## 🛠️ 技术栈 (Technology Stack)

* **核心框架**：Spring Boot 3.3.5
* **持久层**：MyBatis-Plus 3.5.7 + MySQL 8.0
* **中间件**：
    * **Redis**：缓存菜单数据
    * **Redisson**：分布式锁控制并发
    * **RabbitMQ**：订单请求异步化
* **安全与规范**：
    * **JWT**：基于 Auth0 实现无状态鉴权
    * **BCrypt**：密码加盐哈希存储
    * **GlobalException**：全局统一异常拦截与标准响应体封装

## 🚀 快速启动 (One-Click Deployment)

### 1. 编译打包
在项目根目录下，使用 Maven 将项目打成 Jar 包：
```bash
mvn clean package
```

### 2. 启动全栈服务
通过 Docker Compose 一键构建镜像并启动所有容器：
```bash
docker-compose up -d --build
```
> **注意**：项目已配置华为云高速镜像节点，确保国内环境下镜像拉取秒级完成。

### 3. 数据库初始化
* 连接 `localhost:3307` (Docker 映射端口)。
* 执行 SQL 脚本初始化 `user`, `product`, `orders`, `order_item` 表。

## 🧪 架构验证 (Verification)

* **缓存测试**：访问 `/product/list`，观察控制台输出 `🟢 缓存命中` 或 `🔴 缓存未命中`。
* **压测验证**：使用 Postman 开启 50 并发下单，观察 RabbitMQ 后台 `order.queue` 的消息堆积与平滑消费过程。
* **安全拦截**：未携带 `Authorization` 请求头访问接口，应返回 `400` 并提示“保安：站住！”。
