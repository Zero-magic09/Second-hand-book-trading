package com.campusbookloop.service;

import com.campusbookloop.dto.BookDTO;
import com.campusbookloop.entity.Book;
import com.campusbookloop.entity.Favorite;
import com.campusbookloop.entity.User;
import com.campusbookloop.repository.BookRepository;
import com.campusbookloop.repository.FavoriteRepository;
import com.campusbookloop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    
    private final FavoriteRepository favoriteRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    
    /**
     * 添加收藏
     */
    @Transactional
    public void addFavorite(Long userId, Long bookId) {
        if (favoriteRepository.existsByUserIdAndBookId(userId, bookId)) {
            throw new RuntimeException("已收藏过此书籍");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("书籍不存在"));
        
        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setBook(book);
        favoriteRepository.save(favorite);
        
        // 更新书籍收藏数
        book.setFavoriteCount(book.getFavoriteCount() + 1);
        bookRepository.save(book);
    }
    
    /**
     * 取消收藏
     */
    @Transactional
    public void removeFavorite(Long userId, Long bookId) {
        favoriteRepository.deleteByUserIdAndBookId(userId, bookId);
        
        // 更新书籍收藏数
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book != null && book.getFavoriteCount() > 0) {
            book.setFavoriteCount(book.getFavoriteCount() - 1);
            bookRepository.save(book);
        }
    }
    
    /**
     * 检查是否收藏
     */
    public boolean isFavorited(Long userId, Long bookId) {
        return favoriteRepository.existsByUserIdAndBookId(userId, bookId);
    }
    
    /**
     * 获取用户收藏列表
     */
    public Page<BookDTO> getUserFavorites(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return favoriteRepository.findByUserId(userId, pageable)
                .map(favorite -> {
                    BookDTO dto = new BookDTO();
                    BeanUtils.copyProperties(favorite.getBook(), dto);
                    dto.setSellerId(favorite.getBook().getSeller().getId());
                    dto.setSellerNickname(favorite.getBook().getSeller().getNickname());
                    dto.setIsFavorited(true);
                    return dto;
                });
    }
}
