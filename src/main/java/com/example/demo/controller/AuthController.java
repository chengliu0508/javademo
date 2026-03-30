package com.example.demo.controller;

import com.example.demo.service.AuthService;
import com.example.demo.vo.LoginRequestVO;
import com.example.demo.vo.LoginResponseVO;
import com.example.demo.vo.LogoutResponseVO;
import com.example.demo.vo.MeResponseVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestVO req) {
        try {
            return ResponseEntity.ok(authService.login(req));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        String token = auth != null && auth.startsWith("Bearer ") ? auth.substring("Bearer ".length()).trim() : null;
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(java.util.Map.of("error", "missing token"));
        }
        try {
            boolean ok = authService.logout(token);
            return ResponseEntity.ok(new LogoutResponseVO(ok));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestAttribute(name = AuthInterceptor.ATTR_CURRENT_USER_ID) Long currentUserId) {
        try {
            return ResponseEntity.ok(authService.me(currentUserId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(java.util.Map.of("error", e.getMessage()));
        }
    }
}

