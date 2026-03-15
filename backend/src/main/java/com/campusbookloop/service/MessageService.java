package com.campusbookloop.service;

import com.campusbookloop.dto.*;
import com.campusbookloop.entity.*;
import com.campusbookloop.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    
    private final MessageRepository messageRepository;
    private final ConversationRepository conversationRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    
    /**
     * 发送消息
     */
    @Transactional
    public MessageDTO sendMessage(Long senderId, Long receiverId, Long bookId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("发送者不存在"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("接收者不存在"));
        Book book = null;
        if (bookId != null) {
            book = bookRepository.findById(bookId).orElse(null);
        }
        
        // 创建消息
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setBook(book);
        message.setContent(content);
        message.setType(0);  // 文本消息
        message.setIsRead(0);  // 未读
        
        Message savedMessage = messageRepository.save(message);
        
        // 更新或创建会话
        updateConversation(sender, receiver, book, content);
        
        return convertToDTO(savedMessage);
    }
    
    /**
     * 更新会话
     */
    private void updateConversation(User user1, User user2, Book book, String lastMessage) {
        Conversation conversation = conversationRepository.findByUsers(user1.getId(), user2.getId())
                .orElseGet(() -> {
                    Conversation c = new Conversation();
                    c.setUser1(user1);
                    c.setUser2(user2);
                    c.setBook(book);
                    c.setUnreadCount1(0);
                    c.setUnreadCount2(0);
                    return c;
                });
        
        conversation.setLastMessage(lastMessage);
        conversation.setLastMessageTime(LocalDateTime.now());
        
        // 增加接收方未读数
        if (conversation.getUser1().getId().equals(user2.getId())) {
            conversation.setUnreadCount1(conversation.getUnreadCount1() + 1);
        } else {
            conversation.setUnreadCount2(conversation.getUnreadCount2() + 1);
        }
        
        conversationRepository.save(conversation);
    }
    
    /**
     * 获取会话列表
     */
    public Page<ConversationDTO> getConversations(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return conversationRepository.findByUserId(userId, pageable)
                .map(c -> convertConversationToDTO(c, userId));
    }
    
    /**
     * 获取会话消息
     */
    public List<MessageDTO> getConversationMessages(Long userId, Long otherUserId) {
        // 标记消息为已读
        messageRepository.markAsRead(userId, otherUserId);
        
        // 清除会话未读数
        conversationRepository.findByUsers(userId, otherUserId)
                .ifPresent(c -> {
                    if (c.getUser1().getId().equals(userId)) {
                        c.setUnreadCount1(0);
                    } else {
                        c.setUnreadCount2(0);
                    }
                    conversationRepository.save(c);
                });
        
        return messageRepository.findConversationMessages(userId, otherUserId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * 获取未读消息数
     */
    public Long getUnreadCount(Long userId) {
        Long count = conversationRepository.getTotalUnreadCount(userId);
        return count != null ? count : 0L;
    }
    
    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderNickname(message.getSender().getNickname());
        dto.setSenderAvatar(message.getSender().getAvatarUrl());
        dto.setReceiverId(message.getReceiver().getId());
        dto.setReceiverNickname(message.getReceiver().getNickname());
        dto.setReceiverAvatar(message.getReceiver().getAvatarUrl());
        if (message.getBook() != null) {
            dto.setBookId(message.getBook().getId());
            dto.setBookTitle(message.getBook().getTitle());
        }
        dto.setContent(message.getContent());
        dto.setType(message.getType());
        dto.setIsRead(message.getIsRead());
        dto.setCreatedAt(message.getCreatedAt());
        return dto;
    }
    
    private ConversationDTO convertConversationToDTO(Conversation c, Long currentUserId) {
        ConversationDTO dto = new ConversationDTO();
        dto.setId(c.getId());
        
        // 确定对方用户
        User otherUser = c.getUser1().getId().equals(currentUserId) ? c.getUser2() : c.getUser1();
        dto.setOtherUserId(otherUser.getId());
        dto.setOtherUserNickname(otherUser.getNickname());
        dto.setOtherUserAvatar(otherUser.getAvatarUrl());
        
        if (c.getBook() != null) {
            dto.setBookId(c.getBook().getId());
            dto.setBookTitle(c.getBook().getTitle());
        }
        
        dto.setLastMessage(c.getLastMessage());
        dto.setLastMessageTime(c.getLastMessageTime());
        
        // 当前用户的未读数
        dto.setUnreadCount(c.getUser1().getId().equals(currentUserId) ? 
                c.getUnreadCount1() : c.getUnreadCount2());
        
        return dto;
    }
}
