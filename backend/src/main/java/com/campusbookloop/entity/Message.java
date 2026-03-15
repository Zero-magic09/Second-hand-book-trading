package com.campusbookloop.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 消息实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "messages")
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;  // 发送者
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;  // 接收者
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;  // 关联的书籍（可为空）
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;  // 消息内容
    
    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private Integer type;  // 消息类型: 0-文本 1-图片 2-系统消息
    
    @Column(columnDefinition = "TINYINT DEFAULT 0")
    private Integer isRead;  // 是否已读: 0-未读 1-已读
    
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
