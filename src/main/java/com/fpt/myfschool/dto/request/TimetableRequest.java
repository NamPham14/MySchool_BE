package com.fpt.myfschool.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalTime;

@Getter
@Setter
public class TimetableRequest {
    private Integer classId;
    private Integer subjectId;
    private Long teacherId;
    private Integer dayOfWeek;
    private String period;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;
    private String note;
    private Boolean isExam;
}
