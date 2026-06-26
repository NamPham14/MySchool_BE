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
    private Double midtermScore;
    private Double finalScore;
    private Double averageScore;
}
