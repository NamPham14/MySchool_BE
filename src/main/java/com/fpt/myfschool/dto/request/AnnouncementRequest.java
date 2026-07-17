package com.fpt.myfschool.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnnouncementRequest {
    private String title;
    private String content;
    private Integer classId;
}
