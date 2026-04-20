# Takeaway-V2: 企业级外卖交易与鉴权系统架构实战

本项目是一个基于 Spring Boot 3.x 构建的高性能外卖核心后端模块。通过重构传统的单表逻辑，实现了具备“三范式”标准的交易链路、基于 BCrypt 的全加密安全体系以及无状态的 JWT 鉴权闭环。



## 🌟 技术亮点 (Core Highlights)

- **数据库架构进化**：放弃冗余设计，重构为 `orders` (主表) 与 `order_item` (子表) 的标准一对多架构，解决了复杂交易下的数据一致性难题。
- **DTO/VO 隔离模式**：引入数据传输对象（DTO）与视图对象（VO）模式，实现前后端字段的严格解耦与敏感数据（如数据库主键）的脱敏。
- **BCrypt 安全护城河**：集成 `jBcrypt` 实现密码的不可逆加盐哈希，确保即便数据库泄露，用户原始密码依然绝对安全。
- **无状态 JWT 鉴权闭环**：基于 JWT + 自定义拦截器（Interceptor）实现无状态身份验证。
- **防御性上下文传递**：通过 `HttpServletRequest` 属性隐式传递 `currentUserId`，彻底杜绝了前端通过伪造 JSON ID 进行“越权下单”的漏洞。

## 🛠️ 技术栈 (Tech Stack)

- **核心框架**：Spring Boot 3.2.x
- **ORM 框架**：MyBatis-Plus (高性能持久层方案)
- **数据库**：MySQL 8.0
- **安全体系**：BCrypt (密码哈希) / Auth0 JWT (令牌鉴权)
- **工具插件**：Lombok / Maven / Postman

## 🚀 快速启动 (Quick Start)

### 1. 环境准备
确保已安装 JDK 17+, MySQL 8.0, Maven 3.8+。

### 2. 初始化数据库
执行项目路径下 `sql/init.sql` 中的脚本，创建 `user`, `orders`, `order_item` 等表。

### 3. 配置修改
修改 `src/main/resources/application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/your_database
    username: your_username
    password: your_password
```
### 4. 运行项目
```bash
mvn clean install
mvn spring-boot:run
```
