package com.campusbookloop.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookDTO {
    private Long id;
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private BigDecimal originalPrice;
    private BigDecimal price;
    private String condition;
    private String category;
    private String description;
    private List<String> images;
    private Long sellerId;
    private String sellerNickname;
    private String sellerSchool;
    private String sellerAvatar;
    private Integer status;
    private Integer viewCount;
    private Integer favoriteCount;
    private Boolean isFavorited; // 当前用户是否收藏
    private LocalDateTime createdAt;
}
