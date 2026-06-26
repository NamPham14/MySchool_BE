package com.fpt.myfschool.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class AssignmentRequest {
    private Integer classId;
    private Integer subjectId;
    private Long teacherId;
    private String title;
    private String description;
    private String imageUrl;
    private LocalDate dueDate;
}
