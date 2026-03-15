package com.campusbookloop.repository;

import com.campusbookloop.entity.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {
    
    @Query("SELECT c FROM Conversation c WHERE " +
           "(c.user1.id = :userId1 AND c.user2.id = :userId2) OR " +
           "(c.user1.id = :userId2 AND c.user2.id = :userId1)")
    Optional<Conversation> findByUsers(@Param("userId1") Long userId1, 
                                        @Param("userId2") Long userId2);
    
    @Query("SELECT c FROM Conversation c WHERE c.user1.id = :userId OR c.user2.id = :userId " +
           "ORDER BY c.lastMessageTime DESC")
    Page<Conversation> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT SUM(CASE WHEN c.user1.id = :userId THEN c.unreadCount1 ELSE c.unreadCount2 END) " +
           "FROM Conversation c WHERE c.user1.id = :userId OR c.user2.id = :userId")
    Long getTotalUnreadCount(@Param("userId") Long userId);
}
