package com.example.demo.controller;

import com.example.demo.service.UserService;
import com.example.demo.vo.UserCreateRequestVO;
import com.example.demo.vo.UserUpdateRequestVO;
import com.example.demo.vo.UserVO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "20") int size) {
        try {
            List<UserVO> list = userService.list(page, size);
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        try {
            return ResponseEntity.ok(userService.getById(id));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody UserCreateRequestVO req) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(req));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id,
                                     @RequestBody UserUpdateRequestVO req) {
        try {
            return ResponseEntity.ok(userService.update(id, req));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        try {
            userService.delete(id);
            return ResponseEntity.ok(Map.of("deleted", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}

