package com.campusbookloop.repository;

import com.campusbookloop.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

       Page<Book> findByStatus(Integer status, Pageable pageable);

       Page<Book> findBySellerId(Long sellerId, Pageable pageable);

       Page<Book> findByCategory(String category, Pageable pageable);

       Page<Book> findByStatusAndCategory(Integer status, String category, Pageable pageable);

       @Query("SELECT b FROM Book b WHERE b.status = :status AND " +
                     "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                     "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')))")
       Page<Book> searchBooks(@Param("status") Integer status,
                     @Param("keyword") String keyword,
                     Pageable pageable);

       @Query("SELECT b FROM Book b WHERE b.status = 0 ORDER BY b.createdAt DESC")
       List<Book> findLatestBooks(Pageable pageable);

       @Query("SELECT b FROM Book b WHERE b.status = 0 ORDER BY b.viewCount DESC")
       List<Book> findHotBooks(Pageable pageable);

       Long countBySellerId(Long sellerId);

       Long countBySellerIdAndStatus(Long sellerId, Integer status);

       @Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                     "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                     "b.isbn LIKE CONCAT('%', :keyword, '%')")
       Page<Book> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

       @Query("SELECT b FROM Book b WHERE " +
                     "(LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                     "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
                     "b.isbn LIKE CONCAT('%', :keyword, '%')) AND " +
                     "b.category = :category")
       Page<Book> searchByKeywordAndCategory(@Param("keyword") String keyword,
                     @Param("category") String category,
                     Pageable pageable);
}
