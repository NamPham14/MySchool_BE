package com.fpt.myfschool.dto.response;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
public class LeaveResDto {
    private Long id;
    private String studentName;
    private String title;
    private String reason;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private LocalDateTime createdAt;
}