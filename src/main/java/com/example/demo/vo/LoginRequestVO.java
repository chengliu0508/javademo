package com.example.demo.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestVO {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}

