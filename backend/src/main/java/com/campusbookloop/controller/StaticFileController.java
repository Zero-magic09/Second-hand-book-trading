package com.campusbookloop.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 静态文件访问控制器 - 直接提供文件服务
 */
@RestController
@RequestMapping("/uploads")
public class StaticFileController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize().toAbsolutePath();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                MediaType mediaType = org.springframework.http.MediaTypeFactory
                        .getMediaType(resource)
                        .orElse(MediaType.APPLICATION_OCTET_STREAM);

                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
