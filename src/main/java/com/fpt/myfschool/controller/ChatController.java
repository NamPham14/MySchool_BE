package com.fpt.myfschool.controller;
import com.fpt.myfschool.dto.response.APIResponse;
import com.fpt.myfschool.dto.response.ConversationDto;
import com.fpt.myfschool.dto.response.MessageDto;
import com.fpt.myfschool.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import com.fpt.myfschool.security.UserDetailsImpl;
import java.util.List;

@RestController
@RequestMapping("/api/v1/conversations")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    /**
     * [DÀNH CHO HỌC SINH & GIÁO VIÊN]
     * API hiển thị danh sách cuộc trò chuyện (chat)
     */
    @GetMapping
    public ResponseEntity<APIResponse<List<ConversationDto>>> getConversations() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(APIResponse.<List<ConversationDto>>builder()
                .status(HttpStatus.OK.value()).code(1000).message("Thành công")
                .data(chatService.getMyConversations(userDetails.getId())).build());
    }

    /**
     * [DÀNH CHO HỌC SINH & GIÁO VIÊN]
     * API xem tin nhắn trong 1 cuộc trò chuyện
     */
    @GetMapping("/{id}/messages")
    public ResponseEntity<APIResponse<List<MessageDto>>> getMessages(@PathVariable Long id) {
        return ResponseEntity.ok(APIResponse.<List<MessageDto>>builder()
                .status(HttpStatus.OK.value()).code(1000).message("Thành công")
                .data(chatService.getMessagesByConversation(id)).build());
    }

    /**
     * [REST] API Gửi tin nhắn thay thế WebSocket nếu FE không dùng Stomp
     */
    @PostMapping("/{id}/messages")
    public ResponseEntity<APIResponse<MessageDto>> sendMessageREST(
            @PathVariable Long id,
            @RequestBody com.fpt.myfschool.dto.request.ChatMessageRequest request) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MessageDto saved = chatService.saveMessage(id, userDetails.getId(), request.getContent());
        // Broadcast qua WebSocket để các user khác update realtime
        messagingTemplate.convertAndSend("/topic/conversations/" + id, saved);
        return ResponseEntity.ok(APIResponse.<MessageDto>builder()
                .status(HttpStatus.OK.value()).code(1000).message("Thành công")
                .data(saved).build());
    }

    /**
     * [DÀNH CHO ADMIN / TEST]
     * Xóa toàn bộ dữ liệu Chat để test lại từ đầu
     */
    @DeleteMapping("/reset")
    public ResponseEntity<APIResponse<String>> resetChats() {
        chatService.resetAllChats();
        return ResponseEntity.ok(APIResponse.<String>builder()
                .status(HttpStatus.OK.value()).code(1000).message("Đã xóa toàn bộ Chat")
                .data("OK").build());
    }

    /**
     * [WEBSOCKET]
     * Nhận tin nhắn từ Client và Broadcast tới các Client khác trong cùng conversation
     */
    @org.springframework.messaging.handler.annotation.MessageMapping("/chat.sendMessage")
    public void sendMessage(@org.springframework.messaging.handler.annotation.Payload com.fpt.myfschool.dto.request.ChatMessageRequest chatMessage) {
        // Lưu tin nhắn vào Database
        MessageDto savedMessage = chatService.saveMessage(
            chatMessage.getConversationId(), 
            chatMessage.getSenderId(), 
            chatMessage.getContent()
        );
        // Bắn tin nhắn qua WebSocket tới topic của Conversation đó
        messagingTemplate.convertAndSend("/topic/conversations/" + chatMessage.getConversationId(), savedMessage);
    }
}