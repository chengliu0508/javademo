# demo (Spring Boot Auth + User Management)

使用 Spring Boot + MySQL + Flyway，实现：登录/退出（token）和用户管理（新增/修改/查看/删除）。

## 配置

编辑/设置环境变量（推荐）：

- `DB_HOST`：MySQL 地址（默认 `localhost`）
- `DB_PORT`：端口（默认 `3306`）
- `DB_NAME`：数据库名（默认 `javademo`）
- `DB_USER`：用户名（默认 `root`）
- `DB_PASSWORD`：密码（默认 `123456`）

> 说明：`DB_NAME` 需要你的库已创建好（Flyway 不会自动创建数据库）。

启动后会自动执行 Flyway 迁移：`src/main/resources/db/migration/V1__init.sql`，创建 `app_user` 表。
```
$env:JAVA_HOME="D:\java\jdk17"
$env:Path="$env:JAVA_HOME\bin;$env:Path"

```
## 启动

在当前目录执行：

```powershell
mvn -DskipTests clean package

mvn -q -DskipTests spring-boot:run
```

服务默认端口：`http://localhost:8080`

OpenAPI：
- `GET /v3/api-docs`
- `GET /swagger-ui.html`
-  http://localhost:8080/swagger-ui/index.html
## API

Base Path：`/api`

### 登录

`POST /api/auth/login`

请求体：

```json
{ "username": "root", "password": "123456" }
```

返回：`token` + 当前用户信息

### 退出

`POST /api/auth/logout`

Header：`Authorization: Bearer <token>`

### 当前用户

`GET /api/auth/me`

Header：`Authorization: Bearer <token>`

### 用户管理（需要鉴权）

- `GET /api/users?page=&size=`
- `GET /api/users/{id}`
- `POST /api/users`
- `PUT /api/users/{id}`
- `DELETE /api/users/{id}`

