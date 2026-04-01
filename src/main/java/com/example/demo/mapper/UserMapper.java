package com.example.demo.mapper;

import com.example.demo.entity.UserEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class UserMapper {
    private final JdbcTemplate jdbcTemplate;

    public UserMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<UserEntity> userRowMapper = (rs, rowNum) -> {
        UserEntity u = new UserEntity();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setDisplayName(rs.getString("display_name"));
        u.setStatus(rs.getInt("status"));
        u.setCreatedAt(toLocalDateTime(rs.getTimestamp("created_at")));
        u.setUpdatedAt(toLocalDateTime(rs.getTimestamp("updated_at")));
        return u;
    };

    private static LocalDateTime toLocalDateTime(Timestamp ts) {
        return ts == null ? null : ts.toLocalDateTime();
    }

    public Optional<UserEntity> findByUsername(String username) {
        List<UserEntity> rows = jdbcTemplate.query(
                "SELECT * FROM app_user WHERE username = ? LIMIT 1",
                userRowMapper,
                username
        );
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    public Optional<UserEntity> findById(Long id) {
        List<UserEntity> rows = jdbcTemplate.query(
                "SELECT * FROM app_user WHERE id = ? LIMIT 1",
                userRowMapper,
                id
        );
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    public List<UserEntity> list(int page, int size) {
        int offset = Math.max(0, page) * Math.max(0, size);
        return jdbcTemplate.query(
                "SELECT * FROM app_user ORDER BY id DESC LIMIT ? OFFSET ?",
                userRowMapper,
                size,
                offset
        );
    }

    public long createUser(String username, String passwordHash, String displayName, Integer status) {
        String sql = "INSERT INTO app_user(username, password_hash, display_name, status) VALUES(?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(conn -> {
            var ps = conn.prepareStatement(sql, new String[]{"id"});
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, displayName);
            ps.setInt(4, status == null ? 1 : status);
            return ps;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("Failed to obtain generated user id");
        }
        return key.longValue();
    }

    public int updateUser(Long id, String username, String passwordHash, String displayName, Integer status) {
        // passwordHash can be null => keep old password_hash
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("UPDATE app_user SET ");
        List<String> setClauses = new ArrayList<>();

        if (username != null) {
            setClauses.add("username = ?");
            params.add(username);
        }
        if (passwordHash != null) {
            setClauses.add("password_hash = ?");
            params.add(passwordHash);
        }
        if (displayName != null) {
            setClauses.add("display_name = ?");
            params.add(displayName);
        }
        if (status != null) {
            setClauses.add("status = ?");
            params.add(status);
        }

        if (setClauses.isEmpty()) {
            return 0;
        }

        sql.append(String.join(", ", setClauses)).append(" WHERE id = ?");
        params.add(id);

        return jdbcTemplate.update(sql.toString(), params.toArray());
    }

    public int deleteUser(Long id) {
        return jdbcTemplate.update("DELETE FROM app_user WHERE id = ?", id);
    }

    public boolean usernameExists(String username) {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM app_user WHERE username = ?",
                Integer.class,
                username
        );
        return cnt != null && cnt > 0;
    }

    public int countUsers() {
        Integer cnt = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM app_user",
                Integer.class
        );
        return cnt == null ? 0 : cnt;
    }
}

