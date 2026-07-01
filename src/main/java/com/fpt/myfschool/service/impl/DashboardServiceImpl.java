package com.fpt.myfschool.service.impl;

import com.fpt.myfschool.dto.response.DashboardOverviewResponse;
import com.fpt.myfschool.entity.LeaveRequest;
import com.fpt.myfschool.repository.FeeInvoiceRepository;
import com.fpt.myfschool.repository.LeaveRequestRepository;
import com.fpt.myfschool.repository.SchoolClassRepository;
import com.fpt.myfschool.repository.UserRepository;
import com.fpt.myfschool.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.util.stream.Collectors;
import com.fpt.myfschool.entity.User;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final FeeInvoiceRepository feeInvoiceRepository;

    @Override
    public DashboardOverviewResponse getOverview() {
        long totalStudents = userRepository.countByRolesName("STUDENT");
        long totalTeachers = userRepository.countByRolesName("TEACHER");
        long totalClasses = schoolClassRepository.count();
        long pendingLeaves = leaveRequestRepository.countByStatus(LeaveRequest.LeaveStatus.PENDING);
        BigDecimal totalRevenue = feeInvoiceRepository.sumTotalRevenue(com.fpt.myfschool.entity.FeeInvoice.FeeStatus.PAID);
        if (totalRevenue == null) totalRevenue = BigDecimal.ZERO;

        Map<String, Long> studentsByGrade = new HashMap<>();
        List<User> allStudents = userRepository.findByRolesName("STUDENT");
        for (User student : allStudents) {
            if (student.getSchoolClass() != null && student.getSchoolClass().getName() != null) {
                String className = student.getSchoolClass().getName().trim();
                // Extract numbers from start of class name (e.g. "12A1" -> "12", "10A2" -> "10")
                String gradeStr = className.replaceAll("[^0-9]", "");
                if (gradeStr.length() >= 2) {
                    gradeStr = gradeStr.substring(0, 2);
                } else if (gradeStr.isEmpty()) {
                    gradeStr = "Khác";
                }
                
                String key = "Khối " + gradeStr;
                studentsByGrade.put(key, studentsByGrade.getOrDefault(key, 0L) + 1);
            }
        }
        
        List<DashboardOverviewResponse.StudentByGradeData> studentChart = studentsByGrade.entrySet().stream()
                .map(e -> new DashboardOverviewResponse.StudentByGradeData(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(DashboardOverviewResponse.StudentByGradeData::getName))
                .collect(Collectors.toList());

        List<Object[]> revenueByMonth = feeInvoiceRepository.sumRevenueByMonth();
        List<DashboardOverviewResponse.RevenueByMonthData> revenueChart = revenueByMonth.stream()
                .map(row -> new DashboardOverviewResponse.RevenueByMonthData(
                        "T" + row[0],
                        row[1] != null ? new BigDecimal(row[1].toString()) : BigDecimal.ZERO))
                .collect(Collectors.toList());

        return DashboardOverviewResponse.builder()
                .totalStudents(totalStudents)
                .totalTeachers(totalTeachers)
                .totalClasses(totalClasses)
                .pendingLeaveRequests(pendingLeaves)
                .totalRevenue(totalRevenue)
                .studentChartData(studentChart)
                .revenueChartData(revenueChart)
                .build();
    }
}
