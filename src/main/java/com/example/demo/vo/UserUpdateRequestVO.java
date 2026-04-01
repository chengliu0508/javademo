package com.example.demo.vo;

import lombok.Data;

@Data
public class UserUpdateRequestVO {
    private String username;
    private String password;
    private String displayName;
    private Integer status;
}

