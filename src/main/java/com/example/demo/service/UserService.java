package com.example.demo.service;

import com.example.demo.entity.UserEntity;
import com.example.demo.mapper.UserMapper;
import com.example.demo.vo.UserCreateRequestVO;
import com.example.demo.vo.UserUpdateRequestVO;
import com.example.demo.vo.UserVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserVO> list(int page, int size) {
        return userMapper.list(page, size).stream().map(this::toVO).collect(Collectors.toList());
    }

    public UserVO getById(Long id) {
        return userMapper.findById(id).map(this::toVO)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));
    }

    public UserVO create(UserCreateRequestVO req) {
        if (userMapper.usernameExists(req.getUsername())) {
            throw new IllegalArgumentException("username already exists");
        }

        String hash = passwordEncoder.encode(req.getPassword());
        long id = userMapper.createUser(req.getUsername(), hash, req.getDisplayName(), req.getStatus());
        return getById(id);
    }

    public UserVO update(Long id, UserUpdateRequestVO req) {
        Optional<UserEntity> existing = userMapper.findById(id);
        UserEntity before = existing.orElseThrow(() -> new IllegalArgumentException("user not found"));

        String newUsername = req.getUsername();
        String newPasswordHash = null;
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            newPasswordHash = passwordEncoder.encode(req.getPassword());
        }

        int updated = userMapper.updateUser(
                id,
                newUsername,
                newPasswordHash,
                req.getDisplayName(),
                req.getStatus()
        );

        if (updated == 0) {
            // nothing changed
            return toVO(before);
        }
        return getById(id);
    }

    public void delete(Long id) {
        int deleted = userMapper.deleteUser(id);
        if (deleted == 0) {
            throw new IllegalArgumentException("user not found");
        }
    }

    private UserVO toVO(UserEntity u) {
        UserVO vo = new UserVO();
        vo.setId(u.getId());
        vo.setUsername(u.getUsername());
        vo.setDisplayName(u.getDisplayName());
        vo.setStatus(u.getStatus());
        vo.setCreatedAt(u.getCreatedAt());
        vo.setUpdatedAt(u.getUpdatedAt());
        return vo;
    }
}

