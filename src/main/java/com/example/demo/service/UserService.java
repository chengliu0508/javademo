package com.example.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.entity.UserEntity;
import com.example.demo.mapper.UserMapper;
import com.example.demo.mapper.UserStructMapper;
import com.example.demo.vo.UserCreateRequestVO;
import com.example.demo.vo.UserUpdateRequestVO;
import com.example.demo.vo.UserVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService extends ServiceImpl<UserMapper, UserEntity> {
    private final PasswordEncoder passwordEncoder;
    private final UserStructMapper userStructMapper;

    public UserService(PasswordEncoder passwordEncoder, UserStructMapper userStructMapper) {
        this.passwordEncoder = passwordEncoder;
        this.userStructMapper = userStructMapper;
    }

    public List<UserVO> list(int page, int size) {
        Page<UserEntity> p = new Page<>(Math.max(1, page + 1L), Math.max(1, size));
        Page<UserEntity> result = this.baseMapper.selectPage(
                p,
                new LambdaQueryWrapper<UserEntity>().orderByDesc(UserEntity::getId)
        );
        return result.getRecords().stream().map(userStructMapper::toUserVO).collect(Collectors.toList());
    }

    public UserVO getById(Long id) {
        UserEntity user = this.baseMapper.selectById(id);
        if (user == null) {
            throw new IllegalArgumentException("user not found");
        }
        return userStructMapper.toUserVO(user);
    }

    public UserVO create(UserCreateRequestVO req) {
        Long count = this.baseMapper.selectCount(
                new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getUsername, req.getUsername())
        );
        if (count != null && count > 0) {
            throw new IllegalArgumentException("username already exists");
        }

        String hash = passwordEncoder.encode(req.getPassword());
        UserEntity entity = new UserEntity();
        entity.setUsername(req.getUsername());
        entity.setPasswordHash(hash);
        entity.setDisplayName(req.getDisplayName());
        entity.setStatus(req.getStatus() == null ? 1 : req.getStatus());
        this.baseMapper.insert(entity);
        return getById(entity.getId());
    }

    public UserVO update(Long id, UserUpdateRequestVO req) {
        UserEntity before = this.baseMapper.selectById(id);
        if (before == null) {
            throw new IllegalArgumentException("user not found");
        }

        if (req.getUsername() != null && !req.getUsername().equals(before.getUsername())) {
            Long usernameCount = this.baseMapper.selectCount(
                    new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getUsername, req.getUsername())
            );
            if (usernameCount != null && usernameCount > 0) {
                throw new IllegalArgumentException("username already exists");
            }
        }

        LambdaUpdateWrapper<UserEntity> uw = new LambdaUpdateWrapper<UserEntity>().eq(UserEntity::getId, id);
        boolean hasUpdateField = false;

        if (req.getUsername() != null) {
            uw.set(UserEntity::getUsername, req.getUsername());
            hasUpdateField = true;
        }
        if (req.getPassword() != null && !req.getPassword().isBlank()) {
            uw.set(UserEntity::getPasswordHash, passwordEncoder.encode(req.getPassword()));
            hasUpdateField = true;
        }
        if (req.getDisplayName() != null) {
            uw.set(UserEntity::getDisplayName, req.getDisplayName());
            hasUpdateField = true;
        }
        if (req.getStatus() != null) {
            uw.set(UserEntity::getStatus, req.getStatus());
            hasUpdateField = true;
        }

        if (!hasUpdateField) {
            // nothing changed
            return userStructMapper.toUserVO(before);
        }

        int updated = this.baseMapper.update(null, uw);
        if (updated == 0) {
            return userStructMapper.toUserVO(before);
        }
        return getById(id);
    }

    public void delete(Long id) {
        int deleted = this.baseMapper.deleteById(id);
        if (deleted == 0) {
            throw new IllegalArgumentException("user not found");
        }
    }

}

