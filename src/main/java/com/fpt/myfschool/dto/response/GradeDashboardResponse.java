package com.fpt.myfschool.dto.response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GradeDashboardResponse {
    private AcademicSummaryResponse summary;
    private List<GradeResponse> details;
}
