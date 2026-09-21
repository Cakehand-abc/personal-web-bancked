package com.blog.controller;

import com.blog.common.Result;
import com.blog.service.SysUserService;
import com.blog.vo.LoginDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private com.blog.utils.JwtUtils jwtUtils;

    @Autowired
    private com.blog.service.TokenRedisService tokenRedisService;

    /**
     * 账号密码登录接口 (返回 Access Token 与 Refresh Token，并存入 Redis)
     */
    @PostMapping("/login")
    public Result<com.blog.vo.TokenVO> login(@RequestBody LoginDTO dto) {
        try {
            com.blog.vo.TokenVO tokenVO = sysUserService.loginWithTokens(dto);
            return Result.success(tokenVO);
        } catch (Exception e) {
            return Result.error(401, e.getMessage());
        }
    }

    /**
     * 无感刷新 Token 接口 (利用长效 Refresh Token 换取新的短期 Access Token)
     */
    @PostMapping("/refresh")
    public Result<com.blog.vo.TokenVO> refresh(@RequestBody com.blog.vo.RefreshTokenDTO dto) {
        String refreshToken = dto != null ? dto.getRefreshToken() : null;
        if (!org.springframework.util.StringUtils.hasText(refreshToken)) {
            return Result.error(401, "Refresh Token 不能为空");
        }

        // 1. 基础 JWT 签名与有效期校验
        if (!jwtUtils.validateToken(refreshToken) || !jwtUtils.validateRefreshToken(refreshToken)) {
            return Result.error(401, "Refresh Token 已过期或非法，请重新登录");
        }

        // 2. 校验 Redis 中是否存在该 Refresh Token (防重放/注销拦截，不存在则直接拒绝)
        String username = tokenRedisService.getUsernameByRefreshToken(refreshToken);
        if (!org.springframework.util.StringUtils.hasText(username)) {
            return Result.error(401, "Refresh Token 已失效或已被注销，请重新登录");
        }

        // 3. 轮换机制 (Refresh Token Rotation): 签发新的 Access Token 和 Refresh Token
        String newAccessToken = jwtUtils.generateAccessToken(username);
        String newRefreshToken = jwtUtils.generateRefreshToken(username);

        // 4. 将旧 Refresh Token 作废，存储新 Refresh Token
        tokenRedisService.deleteRefreshToken(refreshToken);
        tokenRedisService.storeRefreshToken(username, newRefreshToken, com.blog.utils.JwtUtils.REFRESH_TOKEN_EXPIRE_TIME);

        com.blog.vo.TokenVO tokenVO = com.blog.vo.TokenVO.builder()
                .token(newAccessToken)
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(com.blog.utils.JwtUtils.ACCESS_TOKEN_EXPIRE_TIME / 1000)
                .username(username)
                .build();

        return Result.success(tokenVO);
    }

    /**
     * 注销登录接口 (将 Access Token 加入 Redis 黑名单，并清除 Refresh Token)
     */
    @PostMapping("/logout")
    public Result<String> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody(required = false) com.blog.vo.RefreshTokenDTO dto) {
        
        String username = null;
        // 1. 作废当前 Access Token (拉入黑名单)
        if (org.springframework.util.StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            long remainingMs = jwtUtils.getRemainingExpirationMs(accessToken);
            if (remainingMs > 0) {
                tokenRedisService.blacklistAccessToken(accessToken, remainingMs);
            }
            try {
                username = jwtUtils.getUsernameFromToken(accessToken);
            } catch (Exception ignored) {
            }
        }

        // 2. 作废 Refresh Token (优先使用传入的 refreshToken，同时根据 username 清理)
        if (dto != null && org.springframework.util.StringUtils.hasText(dto.getRefreshToken())) {
            tokenRedisService.deleteRefreshToken(dto.getRefreshToken());
        }
        if (org.springframework.util.StringUtils.hasText(username)) {
            tokenRedisService.deleteRefreshTokenByUsername(username);
        }

        return Result.success("退出登录成功，令牌已注销作废");
    }

    /**
     * 一键初始化超级管理员账号 (只能调一次)
     */
    @GetMapping("/init")
    public Result<String> init() {
        try {
            sysUserService.initMasterAccount();
            return Result.success("初始化超级管理员成功！账号: admin, 密码: 您设置的密码");
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }
}
