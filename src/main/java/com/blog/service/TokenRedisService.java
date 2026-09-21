package com.blog.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TokenRedisService {

    @Autowired(required = false)
    private StringRedisTemplate redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "auth:refresh:";
    private static final String USER_REFRESH_PREFIX = "auth:user_refresh:";
    private static final String BLACKLIST_PREFIX = "auth:blacklist:";

    // 内存兜底存储（防止开发/单测环境中 Redis 临时未启动时整个系统崩溃）
    private final Map<String, Long> memoryBlacklist = new ConcurrentHashMap<>();
    private final Map<String, String> memoryRefreshTokens = new ConcurrentHashMap<>();
    private final Map<String, Long> memoryRefreshExpirations = new ConcurrentHashMap<>();

    /**
     * 存储 Refresh Token (关联用户名)
     */
    public void storeRefreshToken(String username, String refreshToken, long durationMs) {
        if (!StringUtils.hasText(refreshToken) || !StringUtils.hasText(username)) {
            return;
        }
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + refreshToken, username, durationMs, TimeUnit.MILLISECONDS);
                redisTemplate.opsForValue().set(USER_REFRESH_PREFIX + username, refreshToken, durationMs, TimeUnit.MILLISECONDS);
                return;
            }
        } catch (Exception e) {
            log.warn("Redis 存储 Refresh Token 异常，切换至内存兜底: {}", e.getMessage());
        }
        // 内存兜底
        long expireAt = System.currentTimeMillis() + durationMs;
        memoryRefreshTokens.put(refreshToken, username);
        memoryRefreshExpirations.put(refreshToken, expireAt);
    }

    /**
     * 校验并根据 Refresh Token 获取用户名
     */
    public String getUsernameByRefreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            return null;
        }
        try {
            if (redisTemplate != null) {
                return redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + refreshToken);
            }
        } catch (Exception e) {
            log.warn("Redis 读取 Refresh Token 异常，尝试内存兜底: {}", e.getMessage());
        }
        // 内存兜底
        Long expireAt = memoryRefreshExpirations.get(refreshToken);
        if (expireAt != null && expireAt > System.currentTimeMillis()) {
            return memoryRefreshTokens.get(refreshToken);
        } else {
            memoryRefreshTokens.remove(refreshToken);
            memoryRefreshExpirations.remove(refreshToken);
            return null;
        }
    }

    /**
     * 将 Access Token 加入黑名单 (注销时使用，TTL 为该 Token 剩余有效时间)
     */
    public void blacklistAccessToken(String accessToken, long remainingMs) {
        if (!StringUtils.hasText(accessToken) || remainingMs <= 0) {
            return;
        }
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set(BLACKLIST_PREFIX + accessToken, "1", remainingMs, TimeUnit.MILLISECONDS);
                return;
            }
        } catch (Exception e) {
            log.warn("Redis 加入黑名单异常，切换至内存兜底: {}", e.getMessage());
        }
        memoryBlacklist.put(accessToken, System.currentTimeMillis() + remainingMs);
    }

    /**
     * 检查 Access Token 是否处于黑名单中
     */
    public boolean isBlacklisted(String accessToken) {
        if (!StringUtils.hasText(accessToken)) {
            return false;
        }
        try {
            if (redisTemplate != null) {
                Boolean hasKey = redisTemplate.hasKey(BLACKLIST_PREFIX + accessToken);
                return Boolean.TRUE.equals(hasKey);
            }
        } catch (Exception e) {
            log.warn("Redis 检查黑名单异常，使用内存兜底: {}", e.getMessage());
        }
        Long expireAt = memoryBlacklist.get(accessToken);
        if (expireAt != null) {
            if (expireAt > System.currentTimeMillis()) {
                return true;
            } else {
                memoryBlacklist.remove(accessToken);
            }
        }
        return false;
    }

    /**
     * 删除指定的 Refresh Token (登出或刷新时作废旧令牌)
     */
    public void deleteRefreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            return;
        }
        try {
            if (redisTemplate != null) {
                String username = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + refreshToken);
                redisTemplate.delete(REFRESH_TOKEN_PREFIX + refreshToken);
                if (StringUtils.hasText(username)) {
                    redisTemplate.delete(USER_REFRESH_PREFIX + username);
                }
                return;
            }
        } catch (Exception e) {
            log.warn("Redis 删除 Refresh Token 异常: {}", e.getMessage());
        }
        memoryRefreshTokens.remove(refreshToken);
        memoryRefreshExpirations.remove(refreshToken);
    }

    /**
     * 根据用户名清除该用户当前持有的 Refresh Token
     */
    public void deleteRefreshTokenByUsername(String username) {
        if (!StringUtils.hasText(username)) {
            return;
        }
        try {
            if (redisTemplate != null) {
                String refreshToken = redisTemplate.opsForValue().get(USER_REFRESH_PREFIX + username);
                if (StringUtils.hasText(refreshToken)) {
                    redisTemplate.delete(REFRESH_TOKEN_PREFIX + refreshToken);
                }
                redisTemplate.delete(USER_REFRESH_PREFIX + username);
                return;
            }
        } catch (Exception e) {
            log.warn("Redis 按用户名删除 Refresh Token 异常: {}", e.getMessage());
        }
        for (Map.Entry<String, String> entry : memoryRefreshTokens.entrySet()) {
            if (username.equals(entry.getValue())) {
                memoryRefreshTokens.remove(entry.getKey());
                memoryRefreshExpirations.remove(entry.getKey());
            }
        }
    }
}
