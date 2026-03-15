package com.campusbookloop.repository;

import com.campusbookloop.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    @Query("SELECT m FROM Message m WHERE " +
           "(m.sender.id = :userId1 AND m.receiver.id = :userId2) OR " +
           "(m.sender.id = :userId2 AND m.receiver.id = :userId1) " +
           "ORDER BY m.createdAt ASC")
    List<Message> findConversationMessages(@Param("userId1") Long userId1, 
                                           @Param("userId2") Long userId2);
    
    @Query("SELECT m FROM Message m WHERE " +
           "(m.sender.id = :userId1 AND m.receiver.id = :userId2) OR " +
           "(m.sender.id = :userId2 AND m.receiver.id = :userId1) " +
           "ORDER BY m.createdAt DESC")
    Page<Message> findConversationMessagesPaged(@Param("userId1") Long userId1, 
                                                 @Param("userId2") Long userId2,
                                                 Pageable pageable);
    
    Long countByReceiverIdAndIsRead(Long receiverId, Integer isRead);
    
    @Modifying
    @Query("UPDATE Message m SET m.isRead = 1 WHERE m.receiver.id = :receiverId AND m.sender.id = :senderId AND m.isRead = 0")
    int markAsRead(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId);
}
