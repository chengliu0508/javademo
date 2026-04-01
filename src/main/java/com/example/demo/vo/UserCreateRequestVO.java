package com.example.demo.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserCreateRequestVO {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private String displayName;
    private Integer status;
}

