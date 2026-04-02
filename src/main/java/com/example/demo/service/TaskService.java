package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.TaskAssigneeEntity;
import com.example.demo.entity.TaskEntity;
import com.example.demo.mapper.TaskAssigneeMapper;
import com.example.demo.mapper.TaskMapper;
import com.example.demo.convert.TaskStructMapper;
import com.example.demo.vo.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService extends ServiceImpl<TaskMapper, TaskEntity> {

    private final TaskAssigneeMapper taskAssigneeMapper;
    private final TaskStructMapper taskStructMapper;

    public TaskService(TaskAssigneeMapper taskAssigneeMapper, TaskStructMapper taskStructMapper) {
        this.taskAssigneeMapper = taskAssigneeMapper;
        this.taskStructMapper = taskStructMapper;
    }

    // ── Create ────────────────────────────────────────────────────────────────

    @Transactional
    public TaskVO createTask(Long creatorId, TaskCreateRequestVO req) {
        TaskEntity task = new TaskEntity();
        task.setTitle(req.getTitle());
        task.setContent(req.getContent());
        task.setCreatorId(creatorId);
        task.setMode(req.getMode());
        task.setStatus("IN_PROGRESS");
        this.baseMapper.insert(task);

        insertAssignees(task.getId(), req.getAssigneeIds());
        return buildTaskVO(task);
    }

    // ── Update ────────────────────────────────────────────────────────────────

    @Transactional
    public TaskVO updateTask(Long taskId, TaskUpdateRequestVO req) {
        TaskEntity task = requireTask(taskId);

        if (req.getTitle() != null)   task.setTitle(req.getTitle());
        if (req.getContent() != null) task.setContent(req.getContent());
        if (req.getStatus() != null)  task.setStatus(req.getStatus());
        this.baseMapper.updateById(task);

        if (req.getAssigneeIds() != null) {
            // Replace assignees: delete existing PENDING ones, insert new ones
            taskAssigneeMapper.delete(new LambdaQueryWrapper<TaskAssigneeEntity>()
                    .eq(TaskAssigneeEntity::getTaskId, taskId)
                    .eq(TaskAssigneeEntity::getStatus, "PENDING"));
            insertAssignees(taskId, req.getAssigneeIds());
        }

        return getTaskDetail(taskId);
    }

    // ── Delete ────────────────────────────────────────────────────────────────

    @Transactional
    public void deleteTask(Long taskId) {
        requireTask(taskId);
        taskAssigneeMapper.delete(new LambdaQueryWrapper<TaskAssigneeEntity>()
                .eq(TaskAssigneeEntity::getTaskId, taskId));
        this.baseMapper.deleteById(taskId);
    }

    // ── Detail ────────────────────────────────────────────────────────────────

    public TaskVO getTaskDetail(Long taskId) {
        TaskEntity task = requireTask(taskId);
        return buildTaskVO(task);
    }

    // ── Query (paginated) ─────────────────────────────────────────────────────

    public IPage<TaskVO> queryTasks(TaskQueryRequestVO req) {
        // MyBatis-Plus 页码从 1 开始；查询参数 page 从 0 开始（与 User 列表一致）
        Page<TaskEntity> page = new Page<>(Math.max(1, req.getPage() + 1L), Math.max(1, req.getSize()));
        IPage<TaskEntity> entityPage = this.baseMapper.selectPageWithAssignee(
                page,
                req.getStatus(),
                req.getCreatorId(),
                req.getMode(),
                req.getKeyword(),
                req.getAssigneeId()
        );
        return entityPage.convert(this::buildTaskVO);
    }

    // ── Assignee Feedback ─────────────────────────────────────────────────────

    /**
     * 反馈人提交反馈，并根据任务模式判断是否自动更新任务状态。
     *
     * PARALLEL（并行）：每人独立，所有人都响应（COMPLETED 或 REJECTED）后主任务变为 COMPLETED。
     * COLLABORATIVE（协作）：所有人必须 COMPLETED，主任务才变 COMPLETED；只要有一人 REJECTED 则变 CANCELLED。
     */
    @Transactional
    public TaskVO submitFeedback(Long taskId, Long assigneeId, AssigneeFeedbackRequestVO req) {
        TaskEntity task = requireTask(taskId);

        if ("COMPLETED".equals(task.getStatus()) || "CANCELLED".equals(task.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Task is already " + task.getStatus());
        }

        TaskAssigneeEntity record = taskAssigneeMapper.selectOne(
                new LambdaQueryWrapper<TaskAssigneeEntity>()
                        .eq(TaskAssigneeEntity::getTaskId, taskId)
                        .eq(TaskAssigneeEntity::getAssigneeId, assigneeId)
                        .eq(TaskAssigneeEntity::getStatus, "PENDING")
        );
        if (record == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No pending assignee record found for current user");
        }

        record.setStatus(req.getStatus());
        record.setFeedback(req.getFeedback());
        record.setCompletedAt(LocalDateTime.now());
        taskAssigneeMapper.updateById(record);

        recalculateTaskStatus(task);
        return getTaskDetail(taskId);
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private void insertAssignees(Long taskId, List<Long> assigneeIds) {
        for (Long uid : assigneeIds) {
            TaskAssigneeEntity a = new TaskAssigneeEntity();
            a.setTaskId(taskId);
            a.setAssigneeId(uid);
            a.setStatus("PENDING");
            taskAssigneeMapper.insert(a);
        }
    }

    private TaskVO buildTaskVO(TaskEntity task) {
        TaskVO vo = taskStructMapper.toTaskVO(task);
        List<TaskAssigneeEntity> assignees = taskAssigneeMapper.selectList(
                new LambdaQueryWrapper<TaskAssigneeEntity>()
                        .eq(TaskAssigneeEntity::getTaskId, task.getId())
                        .orderByAsc(TaskAssigneeEntity::getId)
        );
        vo.setAssignees(assignees.stream()
                .map(taskStructMapper::toAssigneeVO)
                .collect(Collectors.toList()));
        return vo;
    }

    private TaskEntity requireTask(Long taskId) {
        TaskEntity task = this.baseMapper.selectById(taskId);
        if (task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }
        return task;
    }

    /**
     * 根据所有分配人的当前状态重新计算并更新主任务状态。
     */
    private void recalculateTaskStatus(TaskEntity task) {
        List<TaskAssigneeEntity> all = taskAssigneeMapper.selectList(
                new LambdaQueryWrapper<TaskAssigneeEntity>()
                        .eq(TaskAssigneeEntity::getTaskId, task.getId())
        );

        long pendingCount   = all.stream().filter(a -> "PENDING".equals(a.getStatus())).count();
        long rejectedCount  = all.stream().filter(a -> "REJECTED".equals(a.getStatus())).count();
        long completedCount = all.stream().filter(a -> "COMPLETED".equals(a.getStatus())).count();

        String newStatus = null;

        if ("COLLABORATIVE".equals(task.getMode())) {
            if (rejectedCount > 0 ) { //&& pendingCount == 0
                // 协作模式：有拒绝 → 取消
                newStatus = "CANCELLED";
            } else if (pendingCount == 0 && completedCount == all.size()) {
                // 全部完成
                newStatus = "COMPLETED";
            }
        } else {
            // PARALLEL：所有人都响应（无论 COMPLETED/REJECTED）→ 完成
            if (pendingCount == 0) {
                newStatus = "COMPLETED";
            }
        }

        if (newStatus != null) {
            this.baseMapper.update(null, new LambdaUpdateWrapper<TaskEntity>()
                    .eq(TaskEntity::getId, task.getId())
                    .set(TaskEntity::getStatus, newStatus));
        }
    }
}
