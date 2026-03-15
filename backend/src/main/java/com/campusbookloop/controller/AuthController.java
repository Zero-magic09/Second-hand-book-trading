package com.campusbookloop.controller;

import com.campusbookloop.dto.*;
import com.campusbookloop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 微信登录
     */
    @PostMapping("/wx-login")
    public ApiResponse<LoginResponse> wxLogin(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.wxLogin(request.getCode());
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 手机号登录
     */
    /**
     * 手机号登录
     */
    @PostMapping("/phone-login")
    public ApiResponse<LoginResponse> phoneLogin(@RequestBody LoginRequest request) {
        try {
            LoginResponse response = userService.phoneLogin(request.getPhone(), request.getVerifyCode());
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 手机号注册
     */
    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(@RequestBody RegisterRequest request) {
        try {
            LoginResponse response = userService.register(
                    request.getPhone(),
                    request.getVerifyCode(),
                    request.getNickname(),
                    request.getSchool(),
                    request.getStudentId());
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
