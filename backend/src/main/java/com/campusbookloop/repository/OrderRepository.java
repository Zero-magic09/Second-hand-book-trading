package com.campusbookloop.repository;

import com.campusbookloop.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    Optional<Order> findByOrderNo(String orderNo);
    
    Page<Order> findByBuyerId(Long buyerId, Pageable pageable);
    
    Page<Order> findBySellerId(Long sellerId, Pageable pageable);
    
    Page<Order> findByBuyerIdAndStatus(Long buyerId, Integer status, Pageable pageable);
    
    Page<Order> findBySellerIdAndStatus(Long sellerId, Integer status, Pageable pageable);
    
    List<Order> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);
    
    List<Order> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
    
    Long countByBuyerId(Long buyerId);
    
    Long countBySellerId(Long sellerId);
    
    Long countByBuyerIdAndStatus(Long buyerId, Integer status);
    
    Long countBySellerIdAndStatus(Long sellerId, Integer status);
}
