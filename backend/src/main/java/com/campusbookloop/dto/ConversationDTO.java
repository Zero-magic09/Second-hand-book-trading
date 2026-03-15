package com.campusbookloop.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ConversationDTO {
    private Long id;
    private Long otherUserId;
    private String otherUserNickname;
    private String otherUserAvatar;
    private Long bookId;
    private String bookTitle;
    private String bookImage;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Integer unreadCount;
}
