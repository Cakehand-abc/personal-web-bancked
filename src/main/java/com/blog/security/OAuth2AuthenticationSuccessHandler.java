package com.blog.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.blog.entity.SysUser;
import com.blog.service.SysUserService;
import com.blog.utils.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private com.blog.service.TokenRedisService tokenRedisService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            // 1. 获取 GitHub 返回的用户信息
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = oauth2User.getAttributes();
            
            // GitHub 返回的 ID 是一个数字类型，将其转为字符串
            String githubId = String.valueOf(attributes.get("id"));
            String loginName = (String) attributes.get("login"); // GitHub的用户名
            String avatarUrl = (String) attributes.get("avatar_url"); // 头像

            // 2. 检查数据库中是否已绑定过此 GitHub 账号
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getGithubId, githubId);
            SysUser user = sysUserService.getOne(wrapper);

            if (user == null) {
                // 如果没绑定过，自动为他创建一个专属管理员账号！
                user = new SysUser();
                user.setUsername("github_" + loginName); // 避免与本地账号冲突
                user.setNickname(loginName);
                user.setAvatar(avatarUrl);
                user.setGithubId(githubId);
                // 解决 500 报错：因为数据库里 password 字段是 NOT NULL，所以必须填一个占位符。
                // 填入一个乱码占位符，保证他绝对无法通过这个密码进行本地登录
                user.setPassword("OAUTH2_USER_NO_LOCAL_PASSWORD");
                sysUserService.save(user);
            }

            // 3. 签发双 Token (Access Token 30分钟 + Refresh Token 7天)
            String accessToken = jwtUtils.generateAccessToken(user.getUsername());
            String refreshToken = jwtUtils.generateRefreshToken(user.getUsername());

            // 4. 将 Refresh Token 存入 Redis
            tokenRedisService.storeRefreshToken(user.getUsername(), refreshToken, com.blog.utils.JwtUtils.REFRESH_TOKEN_EXPIRE_TIME);

            // 5. 重定向回前端 Vue 项目，并把 Token 带在 URL 后面
            response.sendRedirect("http://localhost:5173/oauth2/redirect?token=" + accessToken + "&refreshToken=" + refreshToken + "&username=" + user.getUsername());
        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write("<h1>哎呀，出错了！</h1><p>错误信息：" + e.getMessage() + "</p><p>请把这个页面截图发给 Antigravity！</p><p>堆栈追踪：" + e.toString() + "</p>");
        }
    }
}
