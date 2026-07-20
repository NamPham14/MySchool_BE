package com.fpt.myfschool.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubResponse {
    private Long id;
    private String name;
    private String description;
    private String logoUrl;
    private Long leaderId;
    private String leaderName;
    private Integer maxMembers;
    private Integer currentMembers;
    private String status;
    private String membershipStatus;
}
