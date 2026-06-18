package com.blog.config;

import com.blog.security.JwtAuthenticationFilter;
import com.blog.security.OAuth2AuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    /**
     * 核心安全过滤器链配置
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. 关闭 CSRF 防护（前后端分离项目不需要）
            .csrf(csrf -> csrf.disable())
            // 2. 禁用 Session (我们用 JWT 无状态)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 3. 跨域配置交给我们之前写的 CorsConfig
            .cors(cors -> {})
            // 拦截未登录异常，直接返回 401，不要重定向到 GitHub
            .exceptionHandling(e -> e.authenticationEntryPoint((request, response, authException) -> {
                response.setStatus(401);
                response.setContentType("application/json;charset=utf-8");
                response.getWriter().write("{\"code\":401,\"msg\":\"登录已过期，请重新登录\"}");
            }))
            // 4. 路由拦截规则
            .authorizeHttpRequests(auth -> auth
                // 放行所有的 OPTIONS 请求（解决跨域 PUT/POST 请求的预检报错）
                .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                // 所有以 /api/auth/ 开头的接口（比如登录）全部放行
                .requestMatchers("/api/auth/**").permitAll()
                // 列出所有管理端接口（Spring Boot 3 默认不支持在路径中间使用 **，所以我们要枚举出来）
                .requestMatchers(
                    "/api/articles/admin/**",
                    "/api/moments/admin/**",
                    "/api/projects/admin/**",
                    "/api/categories/admin/**",
                    "/api/tags/admin/**",
                    "/api/users/admin/**",
                    "/api/settings/admin/**"
                ).authenticated()
                // 其他前台接口随便访问
                .anyRequest().permitAll()
            )
            // 5. 配置 OAuth2 登录
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oAuth2AuthenticationSuccessHandler) // 登录成功后走我们写的处理器签发 JWT
            )
            // 6. 把我们写的 JWT 过滤器加在 UsernamePassword 过滤器之前
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
