package com.example.demo.vo;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class TaskUpdateRequestVO {

    private String title;

    private String content;

    /**
     * 允许手动更新为 CANCELLED
     */
    @Pattern(regexp = "PENDING|IN_PROGRESS|COMPLETED|CANCELLED",
             message = "status must be PENDING|IN_PROGRESS|COMPLETED|CANCELLED")
    private String status;

    /**
     * 重置分配人列表（传入则替换全部，不传则不变）
     */
    private List<Long> assigneeIds;
}
