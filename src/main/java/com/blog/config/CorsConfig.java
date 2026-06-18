package com.blog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // 允许发送 Cookie 和 Authorization 认证信息
        config.setAllowCredentials(true);
        // 允许所有的源（不能直接使用 setAllowedOrigins("*")，需要用 allowedOriginPatterns）
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        // 允许所有的请求头
        config.setAllowedHeaders(Arrays.asList("*"));
        // 允许所有的 HTTP 方法 (GET, POST, PUT, DELETE, OPTIONS 等)
        config.setAllowedMethods(Arrays.asList("*"));
        
        // 将此跨域配置应用到所有的路径
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
