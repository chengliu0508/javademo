package com.example.demo.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.demo.interceptor.AuthInterceptor;
import com.example.demo.service.TaskService;
import com.example.demo.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Task", description = "代办任务管理（并行 / 协作审批）")
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "新建任务")
    @PostMapping
    public ResponseEntity<TaskVO> createTask(
            @RequestAttribute(name = AuthInterceptor.ATTR_CURRENT_USER_ID) Long currentUserId,
            @Valid @RequestBody TaskCreateRequestVO req) {
        return ResponseEntity.ok(taskService.createTask(currentUserId, req));
    }

    @Operation(summary = "编辑任务（标题/内容/状态/分配人）")
    @PutMapping("/{id}")
    public ResponseEntity<TaskVO> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskUpdateRequestVO req) {
        return ResponseEntity.ok(taskService.updateTask(id, req));
    }

    @Operation(summary = "删除任务")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "任务详情")
    @GetMapping("/{id}")
    public ResponseEntity<TaskVO> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskDetail(id));
    }

    @Operation(summary = "分页查询任务（支持按状态/分配人/创建人/模式/关键词筛选）")
    @GetMapping
    public ResponseEntity<IPage<TaskVO>> queryTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        TaskQueryRequestVO req = new TaskQueryRequestVO();
        req.setStatus(status);
        req.setAssigneeId(assigneeId);
        req.setCreatorId(creatorId);
        req.setMode(mode);
        req.setKeyword(keyword);
        req.setPage(page);
        req.setSize(size);
        return ResponseEntity.ok(taskService.queryTasks(req));
    }

    @Operation(summary = "分配人提交反馈（COMPLETED / REJECTED）")
    @PostMapping("/{id}/feedback")
    public ResponseEntity<TaskVO> submitFeedback(
            @PathVariable Long id,
            @RequestAttribute(name = AuthInterceptor.ATTR_CURRENT_USER_ID) Long currentUserId,
            @Valid @RequestBody AssigneeFeedbackRequestVO req) {
        return ResponseEntity.ok(taskService.submitFeedback(id, currentUserId, req));
    }
}
