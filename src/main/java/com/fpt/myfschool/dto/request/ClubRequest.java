package com.fpt.myfschool.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClubRequest {
    private String name;
    private String description;
    private String logoUrl;
    private Long leaderId;
    private Integer maxMembers;
    private String status;
}
