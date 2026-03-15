package com.campusbookloop.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.annotation.PostConstruct;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    private String absoluteUploadPath;

    @PostConstruct
    public void init() {
        // 计算绝对路径并打印日志
        absoluteUploadPath = Paths.get(uploadDir).toAbsolutePath().normalize().toString();
        System.out.println("=== 上传文件目录: " + absoluteUploadPath + " ===");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上传文件的访问路径
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        // 关键修复：
        // 1. 统一使用正斜杠 /
        // 2. 必须以 file:/// 开头
        // 3. 目录路径必须以 / 结尾
        String pathStr = uploadPath.toString().replace("\\", "/");
        if (!pathStr.endsWith("/")) {
            pathStr += "/";
        }
        String uploadLocation = "file:///" + pathStr;

        System.out.println("=== 静态资源映射: /uploads/** -> " + uploadLocation + " ===");

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadLocation);
    }
}
