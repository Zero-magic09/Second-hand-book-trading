package com.campusbookloop.controller;

import com.campusbookloop.dto.*;
import com.campusbookloop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/me")
    public ApiResponse<UserDTO> getCurrentUser(@RequestHeader("X-User-Id") Long userId) {
        try {
            UserDTO user = userService.getUserById(userId);
            return ApiResponse.success(user);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/{userId}")
    public ApiResponse<UserDTO> getUserById(@PathVariable Long userId) {
        try {
            UserDTO user = userService.getUserById(userId);
            return ApiResponse.success(user);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/me")
    public ApiResponse<UserDTO> updateUser(@RequestHeader("X-User-Id") Long userId,
            @RequestBody UserDTO userDTO) {
        try {
            UserDTO user = userService.updateUser(userId, userDTO);
            return ApiResponse.success(user);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 提交学生认证
     */
    @PostMapping("/verification")
    public ApiResponse<UserDTO> submitVerification(@RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody VerificationRequest request) {
        try {
            UserDTO user = userService.submitVerification(userId, request);
            return ApiResponse.success(user);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取待审核认证列表（管理员）
     */
    @GetMapping("/verifications/pending")
    public ApiResponse<List<UserDTO>> getPendingVerifications() {
        try {
            List<UserDTO> users = userService.getPendingVerifications();
            return ApiResponse.success(users);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 审核认证（管理员）
     */
    @PostMapping("/verifications/{userId}/audit")
    public ApiResponse<UserDTO> auditVerification(@PathVariable Long userId,
            @RequestParam boolean approved) {
        try {
            UserDTO user = userService.auditVerification(userId, approved);
            return ApiResponse.success(user);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
