package com.campusbookloop.exception;

import com.campusbookloop.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleRuntimeException(RuntimeException e) {
        return ApiResponse.error(e.getMessage());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        return ApiResponse.error(400, message);
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<Void> handleException(Exception e) {
        return ApiResponse.error("服务器内部错误：" + e.getMessage());
    }
}
