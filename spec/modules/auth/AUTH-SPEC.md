# Auth Module Spec

Base Path: `/api/auth`

## 1) Login

- `POST /api/auth/login`
- Request:
```json
{
  "username": "admin",
  "password": "123456"
}
```
- Response (`LoginResponseVO`):
```json
{
  "token": "jwt-token",
  "userId": 1,
  "username": "admin",
  "displayName": "管理员",
  "status": 1
}
```

## 2) Logout

- `POST /api/auth/logout`
- Header:
  - `Authorization: Bearer <token>`
- Response:
```json
{ "ok": true }
```

## 3) Current User

- `GET /api/auth/me`
- Header:
  - `Authorization: Bearer <token>`
- Response (`MeResponseVO`):
```json
{
  "userId": 1,
  "username": "admin",
  "displayName": "管理员",
  "status": 1
}
```

## Token 说明

- token 为 JWT（HS256），并且 token 在 Redis 中存储了一份“可用性记录”
- Redis key：`auth:jwt:<token>`，value 为 `userId`，TTL 与 JWT `exp` 一致
- `POST /api/auth/logout` 会删除 Redis 中对应 key，使 token 立即失效

