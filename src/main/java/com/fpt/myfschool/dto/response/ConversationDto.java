package com.fpt.myfschool.dto.response;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class ConversationDto {
    private Long id;
    private String name;
    private String type;
    private String lastMessage;
    private LocalDateTime lastUpdated;
    private Integer unreadCount;
}