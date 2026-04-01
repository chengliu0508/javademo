# Project Spec (OpenSpec / OpenAPI)

## 目标

这是一个“项目说明/接口说明”的可维护文件，用来让 AI 在每轮对话开始时快速理解当前项目的结构与功能，并在对话结束时同步更新。

## 当前分层目录（按 controller/service/mapper/vo/entity）

- `controller`：对外 REST 接口（`AuthController`、`UserController`）
- `interceptor`：鉴权拦截器（`AuthInterceptor`）
- `config`：Web MVC 配置（`AuthWebConfig` 实现 `WebMvcConfigurer`）
- `config`：Web MVC + MyBatis-Plus 配置（`AuthWebConfig`、`MybatisPlusConfig`）
- `service`：业务逻辑（`AuthService`、`UserService`、`PasswordEncoderConfig`）
- `mapper`：MySQL 数据访问（`UserMapper`，基于 MyBatis-Plus `BaseMapper`）
- `vo`：请求/响应 DTO（Login/User 的 VO）
- `entity`：数据库模型（`UserEntity`）

## 模块化 Specs（未来可持续扩展）

- `spec/modules/auth/AUTH-SPEC.md`：登录/退出/当前用户信息
- `spec/modules/user/USER-SPEC.md`：用户管理（新增/修改/查看/删除）


## Swagger / OpenAPI（运行时）

已集成 `springdoc-openapi`：
- `GET /v3/api-docs`：OpenAPI JSON
- `GET /swagger-ui.html`：Swagger UI

## 数据库

MySQL：
- 数据库：`javademo`
- 迁移：Flyway（`src/main/resources/db/migration/V1__init.sql`）
- 表：
  - `app_user`：用户信息（用户名唯一，password_hash 使用 BCrypt）

Redis：
- 连接：`127.0.0.1:6379`（可由 `REDIS_HOST/REDIS_PORT/REDIS_PASSWORD` 覆盖）
- 存储：JWT token -> userId（key: `auth:jwt:<token>`，带 TTL）

## 环境信息

- Java：
  - 项目编译目标（`pom.xml` `java.version`）：`17`
- Maven：
  - 当前 shell：`mvn` 3.9.14
- Maven 插件：
  - `spring-boot-maven-plugin`：使用 `spring-boot-starter-parent` 管理的版本
  - `maven-compiler-plugin`：`3.11.0`
- 构建要求：
  - 本项目（Spring Boot 3.2.x）编译必须使用 `JDK 17+`
- 关键依赖（版本来自 `pom.xml`）：
  - Spring Boot：`3.2.5`（parent）
  - springdoc-openapi：`2.5.0`
  - jjwt（JWT）：`0.12.5`
  - mysql-connector-j：`8.3.0`
  - Flyway：
    - `flyway-core`：`9.22.3`
    - `flyway-mysql`：`9.22.3`
  - MyBatis-Plus：
    - `mybatis-plus-spring-boot3-starter`：`3.5.16`
  - spring-boot-starter-data-redis：版本由 Spring Boot parent 管理

