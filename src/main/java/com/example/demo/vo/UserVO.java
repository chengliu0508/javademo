package com.example.demo.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String displayName;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

