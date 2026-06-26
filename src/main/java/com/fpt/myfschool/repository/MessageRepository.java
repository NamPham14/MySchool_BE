package com.fpt.myfschool.repository;

import com.fpt.myfschool.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    /**
     * Lấy toàn bộ tin nhắn trong một khung chat (Conversation).
     * Sắp xếp theo thời gian gửi từ cũ đến mới (chuẩn giao diện Chat).
     */
    List<Message> findByConversationIdOrderBySentAtAsc(Long conversationId);
    
    Integer countByConversationIdAndIsReadFalseAndSenderIdNot(Long conversationId, Long senderId);
}