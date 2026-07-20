package com.fpt.myfschool.dto.request;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GradeRequest {
    private Long studentId;
    private Integer subjectId;
    private Integer semesterId;
    private Double regularScore1;
    private Double regularScore2;
    private Double regularScore3;
    private Double regularScore4;
    private Double midtermScore;
    private Double finalScore;
}
