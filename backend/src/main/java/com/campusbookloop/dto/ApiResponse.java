package com.campusbookloop.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private Integer code;
    private String message;
    private T data;
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }
    
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }
    
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "success", null);
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null);
    }
    
    public static <T> ApiResponse<T> error(Integer code, String message) {
        return new ApiResponse<>(code, message, null);
    }
    
    public static <T> ApiResponse<T> unauthorized(String message) {
        return new ApiResponse<>(401, message, null);
    }
    
    public static <T> ApiResponse<T> forbidden(String message) {
        return new ApiResponse<>(403, message, null);
    }
    
    public static <T> ApiResponse<T> notFound(String message) {
        return new ApiResponse<>(404, message, null);
    }
}
