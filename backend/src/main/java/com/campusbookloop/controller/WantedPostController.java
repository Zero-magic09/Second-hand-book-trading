package com.campusbookloop.controller;

import com.campusbookloop.dto.*;
import com.campusbookloop.service.WantedPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/wanted")
@RequiredArgsConstructor
public class WantedPostController {

    private final WantedPostService wantedPostService;

    /**
     * 发布求购
     */
    @PostMapping
    public ApiResponse<WantedPostDTO> createWantedPost(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody WantedPostCreateRequest request) {
        try {
            WantedPostDTO post = wantedPostService.createWantedPost(userId, request);
            return ApiResponse.success(post);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取求购列表
     */
    @GetMapping
    public ApiResponse<Page<WantedPostDTO>> getWantedPosts(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<WantedPostDTO> posts = wantedPostService.getWantedPosts(status, category, keyword, page, size);
            return ApiResponse.success(posts);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取求购详情
     */
    @GetMapping("/{postId}")
    public ApiResponse<WantedPostDTO> getWantedPostById(@PathVariable Long postId) {
        try {
            WantedPostDTO post = wantedPostService.getWantedPostById(postId);
            return ApiResponse.success(post);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取我的求购
     */
    @GetMapping("/my")
    public ApiResponse<Page<WantedPostDTO>> getMyWantedPosts(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<WantedPostDTO> posts = wantedPostService.getUserWantedPosts(userId, page, size);
            return ApiResponse.success(posts);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新求购状态
     */
    @PutMapping("/{postId}/status")
    public ApiResponse<WantedPostDTO> updateWantedPostStatus(
            @PathVariable Long postId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Integer status) {
        try {
            WantedPostDTO post = wantedPostService.updateWantedPostStatus(postId, userId, status);
            return ApiResponse.success(post);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除求购
     */
    @DeleteMapping("/{postId}")
    public ApiResponse<Void> deleteWantedPost(@PathVariable Long postId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            wantedPostService.deleteWantedPost(postId, userId);
            return ApiResponse.success();
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取求购详情（无需登录）
     */
    @GetMapping("/detail/{postId}")
    public ApiResponse<WantedPostDTO> getWantedPostDetail(@PathVariable Long postId) {
        try {
            WantedPostDTO post = wantedPostService.getWantedPostById(postId);
            return ApiResponse.success(post);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
