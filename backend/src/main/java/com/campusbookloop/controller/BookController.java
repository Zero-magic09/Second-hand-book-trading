package com.campusbookloop.controller;

import com.campusbookloop.dto.*;
import com.campusbookloop.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    /**
     * 发布书籍
     */
    @PostMapping
    public ApiResponse<BookDTO> createBook(@RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody BookCreateRequest request) {
        try {
            BookDTO book = bookService.createBook(userId, request);
            return ApiResponse.success(book);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取书籍详情
     */
    @GetMapping("/{bookId}")
    public ApiResponse<BookDTO> getBookById(@PathVariable Long bookId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            BookDTO book = bookService.getBookById(bookId, userId);
            return ApiResponse.success(book);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取书籍列表
     */
    @GetMapping
    public ApiResponse<Page<BookDTO>> getBooks(
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            Page<BookDTO> books = bookService.getBooks(status, category, keyword, page, size, userId);
            return ApiResponse.success(books);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取用户发布的书籍
     */
    @GetMapping("/seller/{sellerId}")
    public ApiResponse<Page<BookDTO>> getBooksBySeller(
            @PathVariable Long sellerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<BookDTO> books = bookService.getBooksBySeller(sellerId, page, size);
            return ApiResponse.success(books);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取我发布的书籍
     */
    @GetMapping("/my")
    public ApiResponse<Page<BookDTO>> getMyBooks(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<BookDTO> books = bookService.getBooksBySeller(userId, page, size);
            return ApiResponse.success(books);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新书籍
     */
    @PutMapping("/{bookId}")
    public ApiResponse<BookDTO> updateBook(@PathVariable Long bookId,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody BookCreateRequest request) {
        try {
            BookDTO book = bookService.updateBook(bookId, userId, request);
            return ApiResponse.success(book);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 更新书籍状态
     */
    @PutMapping("/{bookId}/status")
    public ApiResponse<BookDTO> updateBookStatus(@PathVariable Long bookId,
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam Integer status) {
        try {
            BookDTO book = bookService.updateBookStatus(bookId, userId, status);
            return ApiResponse.success(book);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 删除书籍（下架）
     */
    @DeleteMapping("/{bookId}")
    public ApiResponse<Void> deleteBook(@PathVariable Long bookId,
            @RequestHeader("X-User-Id") Long userId) {
        try {
            bookService.deleteBook(bookId, userId);
            return ApiResponse.success();
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
