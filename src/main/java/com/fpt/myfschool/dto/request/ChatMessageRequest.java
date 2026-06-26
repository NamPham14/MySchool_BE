package com.fpt.myfschool.dto.request;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ChatMessageRequest {
    private Long conversationId;
    private Long senderId;
    private String content;
}
