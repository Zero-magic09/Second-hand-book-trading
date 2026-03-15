package com.campusbookloop.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserDTO {
    private Long id;
    private String openId;
    private String nickname;
    private String avatarUrl;
    private String phone;
    private String email;
    private String school;
    private String studentId;
    private String realName;
    private Integer verificationStatus;
    private Integer role;
    private LocalDateTime createdAt;

    // 统计数据
    private Long sellingCount;
    private Long soldCount;
    private Long favoriteCount;
}
