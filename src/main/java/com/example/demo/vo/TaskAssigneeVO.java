package com.example.demo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskAssigneeVO {

    private Long id;

    private Long assigneeId;

    /** PENDING | COMPLETED | REJECTED */
    private String status;

    private String feedback;

    private LocalDateTime completedAt;

    private LocalDateTime createdAt;
}
