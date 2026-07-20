package com.fpt.myfschool.dto.response;

import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubMemberResponse {
    private Long id;
    private Long clubId;
    private Long studentId;
    private String studentName;
    private String studentEmail;
    private String status;
    private LocalDate joinDate;
}
