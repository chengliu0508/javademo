# User Management Module Spec

Base Path: `/api/users`

## 1) List Users

- `GET /api/users?page=&size=`
- Response: `List<UserVO>`

## 2) Get User

- `GET /api/users/{id}`
- Response: `UserVO`（不存在返回 404）

## 3) Create User

- `POST /api/users`
- Request (`UserCreateRequestVO`):
```json
{
  "username": "zhangsan",
  "password": "123456",
  "displayName": "张三",
  "status": 1
}
```
- Response: `UserVO`

## 4) Update User

- `PUT /api/users/{id}`
- Request (`UserUpdateRequestVO`，字段可选）：
```json
{
  "username": "newName",
  "password": "newPassword",
  "displayName": "新昵称",
  "status": 1
}
```
- Response: `UserVO`

## 5) Delete User

- `DELETE /api/users/{id}`
- Response:
```json
{ "deleted": true }
```

## 鉴权

- 需要 `Authorization: Bearer <token>`
- 受保护路径：`/api/users/**`
- 例外（首次引导）：当 `app_user` 表为空时，`POST /api/users` 允许不带 token 创建第一个用户；之后会恢复为必须携带 token

