package com.fpt.myfschool.repository;

import com.fpt.myfschool.entity.ConversationParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationParticipantRepository extends JpaRepository<ConversationParticipant, Long> {
    List<ConversationParticipant> findByUserId(Long userId);
    List<ConversationParticipant> findByConversationId(Long conversationId);
    
    // Kiểm tra xem user có trong conversation không
    boolean existsByConversationIdAndUserId(Long conversationId, Long userId);
}
