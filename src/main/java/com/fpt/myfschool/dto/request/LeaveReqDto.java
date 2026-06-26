package com.fpt.myfschool.dto.request;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter @Setter
public class LeaveReqDto {
    private Long studentId;
    private String title;
    private String reason;
    private LocalDate startDate;
    private LocalDate endDate;
}