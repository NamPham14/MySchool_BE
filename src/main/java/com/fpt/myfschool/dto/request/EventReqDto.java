package com.fpt.myfschool.dto.request;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EventReqDto {
    private String title;
    private Long categoryId;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private String location;
    private String imageUrl;
    private String description;
    private String status;
}
