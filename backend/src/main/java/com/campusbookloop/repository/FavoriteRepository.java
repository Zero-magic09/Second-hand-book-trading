package com.campusbookloop.repository;

import com.campusbookloop.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserIdAndBookId(Long userId, Long bookId);

    Page<Favorite> findByUserId(Long userId, Pageable pageable);

    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    @Transactional
    void deleteByUserIdAndBookId(Long userId, Long bookId);

    Long countByUserId(Long userId);

    Long countByBookId(Long bookId);

    @Transactional
    void deleteByBookId(Long bookId);

    java.util.List<Favorite> findByBookId(Long bookId);
}
