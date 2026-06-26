package com.fpt.myfschool.dto.response;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
public class AssignmentDto {
    private Long id;
    private String className;
    private String subjectName;
    private String teacherName;
    private String title;
    private String description;
    private String imageUrl;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
}