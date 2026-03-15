package com.campusbookloop.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MessageDTO {
    private Long id;
    private Long senderId;
    private String senderNickname;
    private String senderAvatar;
    private Long receiverId;
    private String receiverNickname;
    private String receiverAvatar;
    private Long bookId;
    private String bookTitle;
    private String content;
    private Integer type;
    private Integer isRead;
    private LocalDateTime createdAt;
}
