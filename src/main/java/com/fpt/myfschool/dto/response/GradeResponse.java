package com.fpt.myfschool.dto.response;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GradeResponse {
    private Long id;
    private String subjectName;
    private String subjectCode;
    private Double midtermScore;
    private Double finalScore;
    private Double averageScore;
}
