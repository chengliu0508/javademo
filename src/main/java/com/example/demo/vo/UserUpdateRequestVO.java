package com.example.demo.vo;

import jakarta.validation.constraints.NotBlank;

public class UserUpdateRequestVO {
    // 可选：更改用户名（注意唯一性约束）
    private String username;

    // 可选：不传则不修改密码
    private String password;

    private String displayName;
    private Integer status;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

