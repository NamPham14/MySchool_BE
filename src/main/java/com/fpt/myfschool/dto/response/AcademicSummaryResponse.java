package com.fpt.myfschool.dto.response;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcademicSummaryResponse {
    private Long id;
    private Double gpa;
    private String academicPerformance;
    private String conduct;
    private String semesterName;
}
