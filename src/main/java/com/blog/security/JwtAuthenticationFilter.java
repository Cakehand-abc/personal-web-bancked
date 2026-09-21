package com.blog.security;

import com.blog.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private com.blog.service.TokenRedisService tokenRedisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 0. /api/auth/ 下的基础认证路由直接放行，避免注销/刷新被黑名单误拦截
        String requestUri = request.getRequestURI();
        if (requestUri != null && requestUri.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 1. 从请求头中获取 Token (通常格式为 Authorization: Bearer <token>)
        String bearerToken = request.getHeader("Authorization");
        String token = null;
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7);
        }

        // 2. 如果携带了 Token 并且校验通过
        if (StringUtils.hasText(token) && jwtUtils.validateToken(token)) {
            // 校验是否处于 Redis 黑名单中 (例如退出登录后)
            if (tokenRedisService.isBlacklisted(token)) {
                response.setStatus(401);
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().write("{\"code\":401,\"msg\":\"令牌已注销失效，请重新登录\"}");
                return;
            }

            // 获取 Token 里的用户名与类型（确保普通 API 只能用 access token 访问）
            String tokenType = jwtUtils.getTokenType(token);
            if ("refresh".equals(tokenType)) {
                response.setStatus(401);
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().write("{\"code\":401,\"msg\":\"Refresh Token 仅用于刷新令牌，不可直接调用接口\"}");
                return;
            }

            String username = jwtUtils.getUsernameFromToken(token);
            
            // 构建 Spring Security 需要的认证对象，并放入上下文中（代表该请求已登录）
            UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 3. 放行请求，进入下一个过滤器或目标接口
        filterChain.doFilter(request, response);
    }
}
