package com.fpt.myfschool.dto.response;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Long id;
    private String title;
    private String message;
    private Boolean isRead;
    private String type;
    private Long relatedId;
    private LocalDateTime createdAt;
}