package com.example.demo.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
public class TaskCreateRequestVO {

    @NotBlank
    private String title;

    private String content;

    /**
     * PARALLEL(并行) 或 COLLABORATIVE(协作)
     */
    @NotNull
    @Pattern(regexp = "PARALLEL|COLLABORATIVE", message = "mode must be PARALLEL or COLLABORATIVE")
    private String mode;

    /**
     * 分配人用户 ID 列表（至少一人）
     */
    @NotEmpty(message = "assigneeIds must not be empty")
    private List<Long> assigneeIds;
}
