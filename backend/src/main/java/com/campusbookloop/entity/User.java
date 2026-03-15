package com.campusbookloop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true)
    private String openId;  // 微信openId
    
    @Column(length = 50)
    private String nickname;  // 用户昵称
    
    @Column(length = 255)
    private String avatarUrl;  // 头像URL
    
    @Column(length = 20, unique = true)
    private String phone;  // 手机号
    
    @Column(length = 100)
    private String email;  // 邮箱
    
    @Column(length = 100)
    private String school;  // 学校
    
    @Column(length = 50)
    private String studentId;  // 学号
    
    @Column(length = 50)
    private String realName;  // 真实姓名
    
    @Column(length = 255)
    private String studentIdCardUrl;  // 学生证照片URL
    
    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private Integer verificationStatus;  // 认证状态: 0-未认证 1-待审核 2-已认证 3-认证失败
    
    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private Integer role;  // 角色: 0-普通用户 1-管理员
    
    @Column(columnDefinition = "TINYINT DEFAULT 1")
    private Integer status;  // 状态: 0-禁用 1-正常
    
    @Column(updatable = false)
    private LocalDateTime createdAt;  // 创建时间
    
    private LocalDateTime updatedAt;  // 更新时间
    
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
