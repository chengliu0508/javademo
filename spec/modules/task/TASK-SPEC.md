# Task Module Spec

## 概述

代办任务管理，支持将任务分派给多个人员，并提供两种审批模式：

| 模式 | 说明 |
|------|------|
| `PARALLEL`（并行） | 每个分配人拥有独立的审批记录，彼此互不影响；所有人响应（COMPLETED 或 REJECTED）后主任务变为 `COMPLETED` |
| `COLLABORATIVE`（协作） | 所有分配人共享一个任务；全部 COMPLETED → 主任务 `COMPLETED`；有 REJECTED 且无 PENDING → 主任务 `CANCELLED` |

---

## 数据库

### `task`

| 列 | 类型 | 说明 |
|----|------|------|
| id | BIGINT AUTO_INCREMENT | PK |
| title | VARCHAR(200) | 任务标题 |
| content | TEXT | 任务内容 |
| creator_id | BIGINT | 创建人 user.id |
| mode | VARCHAR(20) | `PARALLEL` \| `COLLABORATIVE` |
| status | VARCHAR(20) | `PENDING` \| `IN_PROGRESS` \| `COMPLETED` \| `CANCELLED` |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间（ON UPDATE） |

### `task_assignee`

| 列 | 类型 | 说明 |
|----|------|------|
| id | BIGINT AUTO_INCREMENT | PK |
| task_id | BIGINT | FK → task.id（CASCADE DELETE） |
| assignee_id | BIGINT | 分配人 user.id |
| status | VARCHAR(20) | `PENDING` \| `COMPLETED` \| `REJECTED` |
| feedback | TEXT | 反馈意见 |
| completed_at | TIMESTAMP | 反馈时间 |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |

---

## API 端点

> 所有接口需携带 `Authorization: Bearer <token>`

### POST `/api/tasks` — 新建任务

**Request Body**
```json
{
  "title": "季度报告审批",
  "content": "请在本周内完成审批",
  "mode": "COLLABORATIVE",
  "assigneeIds": [2, 3, 4]
}
```

**Response** `200 OK` → `TaskVO`

---

### PUT `/api/tasks/{id}` — 编辑任务

**Request Body**（全部字段可选）
```json
{
  "title": "新标题",
  "status": "CANCELLED",
  "assigneeIds": [2, 5]
}
```
- `assigneeIds` 若传入，则**替换**当前所有 PENDING 状态分配人（已完成的记录保留）。

**Response** `200 OK` → `TaskVO`

---

### DELETE `/api/tasks/{id}` — 删除任务

**Response** `204 No Content`

---

### GET `/api/tasks/{id}` — 任务详情

**Response** `200 OK` → `TaskVO`（含 `assignees` 列表）

---

### GET `/api/tasks` — 分页查询

**Query Parameters**

| 参数 | 说明 | 示例 |
|------|------|------|
| status | 任务状态过滤 | `IN_PROGRESS` |
| assigneeId | 按分配人 ID | `3` |
| creatorId | 按创建人 ID | `1` |
| mode | 任务模式 | `PARALLEL` |
| keyword | 标题/内容关键词 | `报告` |
| page | 页码（默认 1） | `1` |
| size | 每页条数（默认 10） | `10` |

**Response** `200 OK` → `IPage<TaskVO>`

---

### POST `/api/tasks/{id}/feedback` — 分配人提交反馈

当前登录用户即为反馈人，系统自动检测其 `task_assignee` 记录。

**Request Body**
```json
{
  "status": "COMPLETED",
  "feedback": "已审阅，同意"
}
```

**Response** `200 OK` → `TaskVO`（含最新 assignees 状态）

---

## 状态流转

```
新建
 └─ Task.status = IN_PROGRESS
    ├─ PARALLEL 模式
    │   └─ 每人独立反馈 → 全部响应后 Task → COMPLETED
    └─ COLLABORATIVE 模式
        ├─ 全部 COMPLETED → Task → COMPLETED
        └─ 有 REJECTED 且无 PENDING → Task → CANCELLED
```

---

## VO 一览

### TaskVO
```json
{
  "id": 1,
  "title": "季度报告审批",
  "content": "...",
  "creatorId": 1,
  "mode": "COLLABORATIVE",
  "status": "IN_PROGRESS",
  "assignees": [
    {
      "id": 1,
      "assigneeId": 2,
      "status": "COMPLETED",
      "feedback": "同意",
      "completedAt": "2026-04-01T10:00:00",
      "createdAt": "2026-04-01T09:00:00"
    }
  ],
  "createdAt": "2026-04-01T09:00:00",
  "updatedAt": "2026-04-01T10:00:00"
}
```
