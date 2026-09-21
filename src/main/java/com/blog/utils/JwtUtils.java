package com.blog.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtUtils {

    // 固定的 256 位安全密钥，防止每次重启后端导致旧 Token 失效
    private static final Key key = Keys.hmacShaKeyFor("MySecretKeyForPersonalBlogCakehand2026!@#".getBytes(java.nio.charset.StandardCharsets.UTF_8));
    // Access Token 短有效时间：30 分钟 (1800000 毫秒)
    public static final long ACCESS_TOKEN_EXPIRE_TIME = 30 * 60 * 1000L;
    // Refresh Token 长有效时间：7 天 (604800000 毫秒)
    public static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;

    /**
     * 生成 Access Token (短有效：30 分钟)
     */
    public String generateAccessToken(String username) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .claim("token_type", "access")
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(key)
                .compact();
    }

    /**
     * 生成 Refresh Token (长有效：7 天)
     */
    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRE_TIME);

        return Jwts.builder()
                .setId(UUID.randomUUID().toString())
                .claim("token_type", "refresh")
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(key)
                .compact();
    }

    /**
     * 生成 Token (向后兼容，默认生成短效 Access Token)
     */
    public String generateToken(String username) {
        return generateAccessToken(username);
    }

    /**
     * 解析 Token 获取 Claims
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 解析 Token 并获取用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * 获取 Token 类型 (access / refresh)
     */
    public String getTokenType(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Object type = claims.get("token_type");
            return type != null ? type.toString() : "access";
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取 Token 剩余有效时间（毫秒）
     */
    public long getRemainingExpirationMs(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Date expiration = claims.getExpiration();
            long remaining = expiration.getTime() - System.currentTimeMillis();
            return Math.max(0, remaining);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 校验 Token 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 校验是否为合法的 Refresh Token
     */
    public boolean validateRefreshToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            Object type = claims.get("token_type");
            return "refresh".equals(type);
        } catch (Exception e) {
            return false;
        }
    }
}
