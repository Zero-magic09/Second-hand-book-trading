package com.campusbookloop.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @PostMapping("/image")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();

        if (file.isEmpty()) {
            result.put("code", 400);
            result.put("message", "请选择要上传的文件");
            return ResponseEntity.badRequest().body(result);
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            result.put("code", 400);
            result.put("message", "只能上传图片文件");
            return ResponseEntity.badRequest().body(result);
        }

        try {
            // 创建上传目录
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 返回访问URL
            String fileUrl = "/uploads/" + newFilename;

            result.put("code", 0);
            result.put("message", "上传成功");
            result.put("url", fileUrl);
            result.put("filename", newFilename);

            return ResponseEntity.ok(result);

        } catch (IOException e) {
            result.put("code", 500);
            result.put("message", "上传失败: " + e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
}
