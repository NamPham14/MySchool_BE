package com.fpt.myfschool.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalTime;

@Getter
@Setter
public class TimetableResponse {
    private Long id;
    private String className;
    private String subjectName;
    private String teacherName;
    private Integer dayOfWeek;
    private String period;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;
    private String note;
    private Boolean isExam;
}
