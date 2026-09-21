package com.blog;

import com.blog.common.Result;
import com.blog.controller.AuthController;
import com.blog.service.TokenRedisService;
import com.blog.utils.JwtUtils;
import com.blog.vo.RefreshTokenDTO;
import com.blog.vo.TokenVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class AuthTokenTest {

    private JwtUtils jwtUtils;
    private TokenRedisService tokenRedisService;
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        jwtUtils = new JwtUtils();
        tokenRedisService = new TokenRedisService();
        authController = new AuthController();

        ReflectionTestUtils.setField(authController, "jwtUtils", jwtUtils);
        ReflectionTestUtils.setField(authController, "tokenRedisService", tokenRedisService);
    }

    @Test
    public void testLoggedOutRefreshTokenMustFail() {
        String username = "testuser";
        String refreshToken = jwtUtils.generateRefreshToken(username);

        // Store refresh token
        tokenRedisService.storeRefreshToken(username, refreshToken, JwtUtils.REFRESH_TOKEN_EXPIRE_TIME);
        assertEquals(username, tokenRedisService.getUsernameByRefreshToken(refreshToken));

        // Now user logs out: refresh token is deleted
        tokenRedisService.deleteRefreshToken(refreshToken);
        assertNull(tokenRedisService.getUsernameByRefreshToken(refreshToken));

        // Now attempt to refresh with this revoked/logged-out token
        RefreshTokenDTO dto = new RefreshTokenDTO();
        dto.setRefreshToken(refreshToken);

        Result<TokenVO> result = authController.refresh(dto);
        assertEquals(401, result.getCode(), "Refreshing with a logged-out or revoked token MUST return 401 error!");
    }

    @Test
    public void testRefreshTokenRotationAndReplayPrevention() {
        String username = "rotation_user";
        String refreshToken1 = jwtUtils.generateRefreshToken(username);

        tokenRedisService.storeRefreshToken(username, refreshToken1, JwtUtils.REFRESH_TOKEN_EXPIRE_TIME);

        // First refresh: should succeed
        RefreshTokenDTO dto = new RefreshTokenDTO();
        dto.setRefreshToken(refreshToken1);
        Result<TokenVO> result1 = authController.refresh(dto);

        assertEquals(200, result1.getCode());
        assertNotNull(result1.getData());
        String refreshToken2 = result1.getData().getRefreshToken();
        assertNotEquals(refreshToken1, refreshToken2, "Rotation must issue a new refresh token");

        // Replay attack: using old refreshToken1 again MUST fail with 401!
        Result<TokenVO> replayResult = authController.refresh(dto);
        assertEquals(401, replayResult.getCode(), "Replaying an already rotated refresh token MUST be rejected!");

        // Using new refreshToken2 should succeed
        dto.setRefreshToken(refreshToken2);
        Result<TokenVO> result2 = authController.refresh(dto);
        assertEquals(200, result2.getCode());
    }

    @Test
    public void testAccessTokenCannotBeUsedAsRefreshToken() {
        String username = "access_user";
        String accessToken = jwtUtils.generateAccessToken(username);

        RefreshTokenDTO dto = new RefreshTokenDTO();
        dto.setRefreshToken(accessToken);

        Result<TokenVO> result = authController.refresh(dto);
        assertEquals(401, result.getCode(), "Access token passed as refresh token MUST fail with 401");
    }

    @Test
    public void testLogoutWithHeaderCleansRefreshTokenByUsername() {
        String username = "header_user";
        String accessToken = jwtUtils.generateAccessToken(username);
        String refreshToken = jwtUtils.generateRefreshToken(username);

        tokenRedisService.storeRefreshToken(username, refreshToken, JwtUtils.REFRESH_TOKEN_EXPIRE_TIME);
        assertEquals(username, tokenRedisService.getUsernameByRefreshToken(refreshToken));

        // Logout with Authorization header only (no DTO body)
        Result<String> logoutResult = authController.logout("Bearer " + accessToken, null);
        assertEquals(200, logoutResult.getCode());

        // Access token must be blacklisted
        assertTrue(tokenRedisService.isBlacklisted(accessToken), "Access token must be in blacklist");

        // Refresh token must be revoked
        assertNull(tokenRedisService.getUsernameByRefreshToken(refreshToken), "User refresh token must be revoked");

        // Refreshing with that refresh token must fail
        RefreshTokenDTO dto = new RefreshTokenDTO();
        dto.setRefreshToken(refreshToken);
        Result<TokenVO> refreshResult = authController.refresh(dto);
        assertEquals(401, refreshResult.getCode());
    }
}
