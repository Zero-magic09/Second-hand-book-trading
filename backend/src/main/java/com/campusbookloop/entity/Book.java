package com.campusbookloop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 书籍实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title; // 书名

    @Column(length = 100)
    private String author; // 作者

    @Column(length = 100)
    private String publisher; // 出版社

    @Column(length = 50)
    private String isbn; // ISBN号

    @Column(nullable = false)
    private BigDecimal originalPrice; // 原价

    @Column(nullable = false)
    private BigDecimal price; // 售价

    @Column(name = "book_condition", length = 50)
    private String condition; // 成色: 全新/九成新/八成新/七成新

    @Column(length = 50)
    private String category; // 分类: 专业教材/考试辅导/文学小说/其他

    @Column(columnDefinition = "TEXT")
    private String description; // 描述

    @Column(columnDefinition = "JSON")
    private String images; // 图片URL列表(JSON格式)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller; // 卖家

    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private Integer status; // 状态: 0-在售 1-已预订 2-已售出 3-已下架

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer viewCount; // 浏览量

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer favoriteCount; // 收藏量

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

    @Transient
    public String getCoverImage() {
        if (this.images == null || this.images.equals("[]") || this.images.isEmpty()) {
            return null;
        }
        // Simple string extraction for JSON array ["url"]
        int start = this.images.indexOf("\"");
        if (start < 0)
            return null;
        int end = this.images.indexOf("\"", start + 1);
        if (end < 0)
            return null;
        return this.images.substring(start + 1, end);
    }
}
