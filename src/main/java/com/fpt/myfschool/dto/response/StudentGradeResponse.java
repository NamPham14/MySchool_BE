package com.fpt.myfschool.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentGradeResponse {
    private Long studentId;
    private String studentName;
    private String rollNumber;
    private String avatarUrl;
    
    // Grade info
    private Long gradeId;
    private Double regularScore1;
    private Double regularScore2;
    private Double regularScore3;
    private Double regularScore4;
    private Double midtermScore;
    private Double finalScore;
    private Double averageScore;
}
