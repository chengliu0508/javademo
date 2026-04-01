package com.example.demo.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.demo.entity.UserEntity;
import com.example.demo.mapper.UserMapper;
import com.example.demo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    public static final String ATTR_CURRENT_USER_ID = "currentUserId";

    private final AuthService authService;
    private final UserMapper userMapper;

    public AuthInterceptor(AuthService authService, UserMapper userMapper) {
        this.authService = authService;
        this.userMapper = userMapper;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // Bootstrap: allow first user creation without Authorization.
        // Once there is at least one user, all /api/users/** endpoints require Bearer token.
        String uri = request.getRequestURI();
        if ("/api/users".equals(uri) && "POST".equalsIgnoreCase(request.getMethod())) {
            Long userCount = userMapper.selectCount(new LambdaQueryWrapper<UserEntity>());
            if (userCount == null || userCount == 0) {
                return true;
            }
        }

        String auth = request.getHeader("Authorization");
        if (auth == null || auth.isBlank() || !auth.startsWith("Bearer ")) {
            writeUnauthorized(response, "missing or invalid Authorization header");
            return false;
        }

        String token = auth.substring("Bearer ".length()).trim();
        var userIdOpt = authService.validateToken(token);
        if (userIdOpt.isEmpty()) {
            writeUnauthorized(response, "invalid or expired token");
            return false;
        }

        request.setAttribute(ATTR_CURRENT_USER_ID, userIdOpt.get());
        return true;
    }

    private static void writeUnauthorized(HttpServletResponse response, String msg) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"error\":\"" + escapeJson(msg) + "\"}");
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
