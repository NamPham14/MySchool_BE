package com.fpt.myfschool.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AnnouncementResponse {
    private Long id;
    private String title;
    private String content;
    private Integer classId;
    private String className;
    private Long teacherId;
    private String teacherName;
    private String teacherAvatar;
    private LocalDateTime createdAt;
}
