package com.example.demo.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskVO {

    private Long id;

    private String title;

    private String content;

    private Long creatorId;

    /** PARALLEL | COLLABORATIVE */
    private String mode;

    /** PENDING | IN_PROGRESS | COMPLETED | CANCELLED */
    private String status;

    private List<TaskAssigneeVO> assignees;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
