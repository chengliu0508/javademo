package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.UserEntity;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.UserStructMapper;
import com.example.demo.vo.LoginRequestVO;
import com.example.demo.vo.LoginResponseVO;
import com.example.demo.vo.MeResponseVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserMapper userMapper;
    private final UserStructMapper userStructMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthService(UserMapper userMapper, UserStructMapper userStructMapper, PasswordEncoder passwordEncoder, JwtTokenService jwtTokenService) {
        this.userMapper = userMapper;
        this.userStructMapper = userStructMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    public LoginResponseVO login(LoginRequestVO req) {
        UserEntity user = userMapper.selectOne(
                new LambdaQueryWrapper<UserEntity>()
                        .eq(UserEntity::getUsername, req.getUsername())
                        .last("LIMIT 1")
        );
        if (user == null) {
            throw new IllegalArgumentException("username or password is incorrect");
        }

        if (!passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("username or password is incorrect");
        }

        String token = jwtTokenService.createToken(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getStatus()
        );

        return userStructMapper.toLoginResponse(user, token);
    }

    public boolean logout(String token) {
        return jwtTokenService.logout(token);
    }

    public Optional<Long> validateToken(String token) {
        return jwtTokenService.validateTokenAndGetUserId(token);
    }

    public MeResponseVO me(Long userId) {
        UserEntity user = userMapper.selectById(userId);
        if (user == null) {
            throw new IllegalArgumentException("user not found");
        }

        return userStructMapper.toMeResponse(user);
    }
}

