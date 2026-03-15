package com.campusbookloop.repository;

import com.campusbookloop.entity.WantedPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WantedPostRepository extends JpaRepository<WantedPost, Long> {
    
    Page<WantedPost> findByStatus(Integer status, Pageable pageable);
    
    Page<WantedPost> findByUserId(Long userId, Pageable pageable);
    
    Page<WantedPost> findByCategory(String category, Pageable pageable);
    
    @Query("SELECT w FROM WantedPost w WHERE w.status = :status AND " +
           "LOWER(w.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<WantedPost> searchWantedPosts(@Param("status") Integer status,
                                        @Param("keyword") String keyword,
                                        Pageable pageable);
    
    Long countByUserId(Long userId);
}
