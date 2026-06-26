package com.fpt.myfschool.dto.response;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class EventResDto {
    private Long id;
    private String title;
    private String categoryName;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private String location;
    private String imageUrl;
    private String description;
    private String status;
}