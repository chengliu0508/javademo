package com.example.demo.vo;

import lombok.Data;

@Data
public class LoginResponseVO {
    private String token;
    private Long userId;
    private String username;
    private String displayName;
    private Integer status;
}

