package com.blog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * 将本地的文件夹映射成网络可以直接访问的静态资源 URL
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 当浏览器请求 /uploads/xxx.jpg 时
        // 自动去项目根目录下的 uploads 文件夹找对应的文件
        String uploadPath = System.getProperty("user.dir") + "/uploads/";
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath);
    }
}
