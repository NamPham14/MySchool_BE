package com.fpt.myfschool.controller;

import com.fpt.myfschool.dto.response.APIResponse;
import com.fpt.myfschool.dto.response.DashboardOverviewResponse;
import com.fpt.myfschool.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    public ResponseEntity<APIResponse<DashboardOverviewResponse>> getDashboardOverview() {
        DashboardOverviewResponse overview = dashboardService.getOverview();
        return ResponseEntity.ok(
                APIResponse.<DashboardOverviewResponse>builder()
                        .status(200)
                        .code(200)
                        .message("Thống kê tổng quan lấy thành công")
                        .data(overview)
                        .build()
        );
    }
}
