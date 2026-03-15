package com.campusbookloop.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class WantedPostDTO {
    private Long id;
    private String title;
    private String author;
    private String description;
    private String category;
    private BigDecimal maxPrice;
    private Long userId;
    private String userNickname;
    private String userAvatar;
    private Integer status;
    private LocalDateTime createdAt;
}
