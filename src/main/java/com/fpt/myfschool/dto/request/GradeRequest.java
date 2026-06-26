package com.fpt.myfschool.dto.request;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GradeRequest {
    private Long studentId;
    private Integer subjectId;
    private Integer semesterId;
    private Double midtermScore;
    private Double finalScore;
}
