package com.campusbookloop.controller;

import com.campusbookloop.dto.*;
import com.campusbookloop.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    /**
     * 添加收藏
     */
    @PostMapping("/{bookId}")
    public ApiResponse<Void> addFavorite(@RequestHeader("X-User-Id") Long userId,
            @PathVariable Long bookId) {
        try {
            favoriteService.addFavorite(userId, bookId);
            return ApiResponse.success();
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{bookId}")
    public ApiResponse<Void> removeFavorite(@RequestHeader("X-User-Id") Long userId,
            @PathVariable Long bookId) {
        try {
            favoriteService.removeFavorite(userId, bookId);
            return ApiResponse.success();
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 检查是否收藏
     */
    @GetMapping("/{bookId}/check")
    public ApiResponse<Boolean> isFavorited(@RequestHeader("X-User-Id") Long userId,
            @PathVariable Long bookId) {
        try {
            boolean isFavorited = favoriteService.isFavorited(userId, bookId);
            return ApiResponse.success(isFavorited);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取收藏列表
     */
    @GetMapping
    public ApiResponse<Page<BookDTO>> getUserFavorites(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<BookDTO> books = favoriteService.getUserFavorites(userId, page, size);
            return ApiResponse.success(books);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
