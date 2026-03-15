package com.campusbookloop.controller;

import com.campusbookloop.dto.*;
import com.campusbookloop.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    /**
     * 发送消息
     */
    @PostMapping
    public ApiResponse<MessageDTO> sendMessage(@RequestHeader("X-User-Id") Long userId,
            @RequestParam Long receiverId,
            @RequestParam(required = false) Long bookId,
            @RequestParam String content) {
        try {
            MessageDTO message = messageService.sendMessage(userId, receiverId, bookId, content);
            return ApiResponse.success(message);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取会话列表
     */
    @GetMapping("/conversations")
    public ApiResponse<Page<ConversationDTO>> getConversations(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Page<ConversationDTO> conversations = messageService.getConversations(userId, page, size);
            return ApiResponse.success(conversations);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取与某用户的聊天记录
     */
    @GetMapping("/conversation/{otherUserId}")
    public ApiResponse<List<MessageDTO>> getConversationMessages(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable Long otherUserId) {
        try {
            List<MessageDTO> messages = messageService.getConversationMessages(userId, otherUserId);
            return ApiResponse.success(messages);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }

    /**
     * 获取未读消息数
     */
    @GetMapping("/unread-count")
    public ApiResponse<Long> getUnreadCount(@RequestHeader("X-User-Id") Long userId) {
        try {
            Long count = messageService.getUnreadCount(userId);
            return ApiResponse.success(count);
        } catch (Exception e) {
            return ApiResponse.error(e.getMessage());
        }
    }
}
