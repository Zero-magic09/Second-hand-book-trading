package com.campusbookloop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 求购帖实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wanted_posts")
public class WantedPost {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;  // 标题/书名
    
    @Column(length = 100)
    private String author;  // 作者（可选）
    
    @Column(columnDefinition = "TEXT")
    private String description;  // 详细描述
    
    @Column(length = 50)
    private String category;  // 分类
    
    private BigDecimal maxPrice;  // 期望最高价格
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 发布者
    
    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private Integer status;  // 状态: 0-求购中 1-已找到 2-已关闭
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
