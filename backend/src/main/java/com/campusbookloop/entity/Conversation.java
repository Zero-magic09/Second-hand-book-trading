package com.campusbookloop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 会话实体类 - 用于消息列表展示
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "conversations")
public class Conversation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;  // 用户1
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;  // 用户2
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;  // 关联的书籍（可为空）
    
    @Column(length = 500)
    private String lastMessage;  // 最后一条消息内容
    
    private LocalDateTime lastMessageTime;  // 最后一条消息时间
    
    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer unreadCount1;  // 用户1未读消息数
    
    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer unreadCount2;  // 用户2未读消息数
    
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
