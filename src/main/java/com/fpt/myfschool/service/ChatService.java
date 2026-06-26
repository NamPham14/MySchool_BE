package com.fpt.myfschool.service;
import com.fpt.myfschool.dto.response.ConversationDto;
import com.fpt.myfschool.dto.response.MessageDto;
import java.util.List;

public interface ChatService {
    List<ConversationDto> getMyConversations(Long userId);
    List<MessageDto> getMessagesByConversation(Long conversationId);
    MessageDto saveMessage(Long conversationId, Long senderId, String content);
    
    void resetAllChats();
}