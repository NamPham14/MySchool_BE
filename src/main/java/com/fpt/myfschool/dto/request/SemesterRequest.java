package com.fpt.myfschool.dto.request;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class SemesterRequest {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isCurrent;
}