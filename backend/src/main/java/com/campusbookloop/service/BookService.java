package com.campusbookloop.service;

import com.campusbookloop.dto.*;
import com.campusbookloop.entity.Book;
import com.campusbookloop.entity.User;
import com.campusbookloop.repository.BookRepository;
import com.campusbookloop.repository.FavoriteRepository;
import com.campusbookloop.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final ObjectMapper objectMapper;

    /**
     * 发布书籍
     */
    @Transactional
    public BookDTO createBook(Long sellerId, BookCreateRequest request) {
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Book book = new Book();
        BeanUtils.copyProperties(request, book);
        book.setSeller(seller);
        book.setStatus(0); // 在售
        book.setViewCount(0);
        book.setFavoriteCount(0);

        // 转换图片列表为JSON
        if (request.getImages() != null) {
            try {
                book.setImages(objectMapper.writeValueAsString(request.getImages()));
            } catch (JsonProcessingException e) {
                book.setImages("[]");
            }
        }

        return convertToDTO(bookRepository.save(book), null);
    }

    /**
     * 获取书籍详情
     */
    @Transactional
    public BookDTO getBookById(Long bookId, Long currentUserId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("书籍不存在"));

        // 增加浏览量
        book.setViewCount(book.getViewCount() + 1);
        bookRepository.save(book);

        return convertToDTO(book, currentUserId);
    }

    /**
     * 获取书籍列表（分页）
     */
    public Page<BookDTO> getBooks(Integer status, String category, String keyword,
            int page, int size, Long currentUserId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Book> books;

        if (keyword != null && !keyword.isEmpty()) {
            books = bookRepository.searchBooks(status != null ? status : 0, keyword, pageable);
        } else if (category != null && !category.isEmpty()) {
            books = bookRepository.findByStatusAndCategory(status != null ? status : 0, category, pageable);
        } else {
            books = bookRepository.findByStatus(status != null ? status : 0, pageable);
        }

        return books.map(book -> convertToDTO(book, currentUserId));
    }

    /**
     * 获取用户发布的书籍
     */
    public Page<BookDTO> getBooksBySeller(Long sellerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return bookRepository.findBySellerId(sellerId, pageable)
                .map(book -> convertToDTO(book, null));
    }

    /**
     * 更新书籍
     */
    @Transactional
    public BookDTO updateBook(Long bookId, Long sellerId, BookCreateRequest request) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("书籍不存在"));

        if (!book.getSeller().getId().equals(sellerId)) {
            throw new RuntimeException("无权修改此书籍");
        }

        BeanUtils.copyProperties(request, book, "id", "seller", "status", "viewCount", "favoriteCount", "createdAt");

        if (request.getImages() != null) {
            try {
                book.setImages(objectMapper.writeValueAsString(request.getImages()));
            } catch (JsonProcessingException e) {
                book.setImages("[]");
            }
        }

        return convertToDTO(bookRepository.save(book), null);
    }

    /**
     * 更新书籍状态
     */
    @Transactional
    public BookDTO updateBookStatus(Long bookId, Long sellerId, Integer status) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("书籍不存在"));

        if (!book.getSeller().getId().equals(sellerId)) {
            throw new RuntimeException("无权修改此书籍");
        }

        book.setStatus(status);
        return convertToDTO(bookRepository.save(book), null);
    }

    /**
     * 删除书籍（下架）
     */
    @Transactional
    public void deleteBook(Long bookId, Long sellerId) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("书籍不存在"));

        if (!book.getSeller().getId().equals(sellerId)) {
            throw new RuntimeException("无权删除此书籍");
        }

        book.setStatus(3); // 已下架
        bookRepository.save(book);
    }

    private BookDTO convertToDTO(Book book, Long currentUserId) {
        BookDTO dto = new BookDTO();
        BeanUtils.copyProperties(book, dto);

        dto.setSellerId(book.getSeller().getId());
        dto.setSellerNickname(book.getSeller().getNickname());
        dto.setSellerSchool(book.getSeller().getSchool());
        dto.setSellerAvatar(book.getSeller().getAvatarUrl());

        // 解析图片JSON
        if (book.getImages() != null) {
            try {
                dto.setImages(objectMapper.readValue(book.getImages(), new TypeReference<List<String>>() {
                }));
            } catch (JsonProcessingException e) {
                dto.setImages(Collections.emptyList());
            }
        }

        // 检查当前用户是否收藏
        if (currentUserId != null) {
            dto.setIsFavorited(favoriteRepository.existsByUserIdAndBookId(currentUserId, book.getId()));
        }

        return dto;
    }
}
