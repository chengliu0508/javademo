package com.example.demo.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtTokenService {
    private final StringRedisTemplate redisTemplate;
    private final Key hmacKey;
    private final long ttlSeconds;

    private static final String REDIS_KEY_PREFIX = "auth:jwt:";

    public JwtTokenService(StringRedisTemplate redisTemplate,
                             @Value("${app.jwt.secret}") String secret,
                             @Value("${app.jwt.ttl-seconds}") long ttlSeconds) {
        this.redisTemplate = redisTemplate;
        this.ttlSeconds = ttlSeconds;
        this.hmacKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(Long userId, String username, String displayName, Integer status) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(ttlSeconds);

        String token = Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(exp))
                .claim("username", username)
                .claim("displayName", displayName)
                .claim("status", status)
                .signWith(hmacKey, SignatureAlgorithm.HS256)
                .compact();

        // Store token -> userId for server-side logout / revocation.
        String redisKey = toRedisKey(token);
        redisTemplate.opsForValue().set(redisKey, userId.toString(), Duration.ofSeconds(ttlSeconds));

        return token;
    }

    public Optional<Long> validateTokenAndGetUserId(String token) {
        try {
            // Use parser() API for broader JJWT version compatibility.
            Claims claims = Jwts.parser()
                    .setSigningKey(hmacKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String subject = claims.getSubject();
            if (subject == null || subject.isBlank()) {
                return Optional.empty();
            }

            long userId = Long.parseLong(subject);

            // Check Redis presence (token must not be logged out / revoked).
            String redisKey = toRedisKey(token);
            String storedUserId = redisTemplate.opsForValue().get(redisKey);
            if (storedUserId == null || storedUserId.isBlank()) {
                return Optional.empty();
            }
            if (!storedUserId.equals(String.valueOf(userId))) {
                return Optional.empty();
            }

            return Optional.of(userId);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public boolean logout(String token) {
        String redisKey = toRedisKey(token);
        Boolean deleted = redisTemplate.delete(redisKey);
        return deleted != null && deleted;
    }

    private static String toRedisKey(String token) {
        return REDIS_KEY_PREFIX + token;
    }
}

