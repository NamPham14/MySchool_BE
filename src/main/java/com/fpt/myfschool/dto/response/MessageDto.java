package com.fpt.myfschool.dto.response;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class MessageDto {
    private Long id;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime sentAt;
}