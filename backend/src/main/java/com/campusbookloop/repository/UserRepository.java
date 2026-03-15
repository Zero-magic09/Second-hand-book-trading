package com.campusbookloop.repository;

import com.campusbookloop.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByOpenId(String openId);
    
    Optional<User> findByPhone(String phone);
    
    boolean existsByPhone(String phone);
    
    boolean existsByOpenId(String openId);
    
    List<User> findByVerificationStatus(Integer verificationStatus);
    
    List<User> findByRole(Integer role);
    
    @Query("SELECT u FROM User u WHERE LOWER(u.nickname) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<User> searchByNickname(@Param("keyword") String keyword, Pageable pageable);
}
