package com.fpt.myfschool.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardOverviewResponse {
    private long totalStudents;
    private long totalTeachers;
    private long totalClasses;
    private long pendingLeaveRequests;
    private BigDecimal totalRevenue;
    private List<StudentByGradeData> studentChartData;
    private List<RevenueByMonthData> revenueChartData;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StudentByGradeData {
        private String name;
        private long students;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueByMonthData {
        private String month;
        private BigDecimal revenue;
    }
}
