package com.example.demo.vo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AssigneeFeedbackRequestVO {

    /**
     * 反馈结果: COMPLETED 或 REJECTED
     */
    @NotNull
    @Pattern(regexp = "COMPLETED|REJECTED", message = "status must be COMPLETED or REJECTED")
    private String status;

    private String feedback;
}
