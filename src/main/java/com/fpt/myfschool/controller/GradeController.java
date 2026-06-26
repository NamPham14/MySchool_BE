package com.fpt.myfschool.controller;
import com.fpt.myfschool.dto.request.GradeRequest;
import com.fpt.myfschool.dto.response.APIResponse;
import com.fpt.myfschool.dto.response.GradeDashboardResponse;
import com.fpt.myfschool.dto.response.GradeResponse;
import com.fpt.myfschool.service.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.context.SecurityContextHolder;
import com.fpt.myfschool.security.UserDetailsImpl;
import java.util.List;
import java.util.Arrays;

@RestController
@RequestMapping("/api/grades")
@RequiredArgsConstructor
public class GradeController {
    private final GradeService gradeService;

    private Long getCurrentUserId() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }

    /**
     * [DÀNH CHO HỌC SINH]
     * API trên màn hình App để học sinh xem Bảng điểm và Tổng kết GPA
     */
    @GetMapping("/dashboard")
    public ResponseEntity<APIResponse<GradeDashboardResponse>> getDashboard(
            @RequestParam(required = false) Long studentId, @RequestParam(required = false) Integer semesterId) {
        
        Long sId = getCurrentUserId();
        Integer semId = (semesterId != null) ? semesterId : 1;

        GradeDashboardResponse data = gradeService.getStudentGradeDashboard(sId, semId);

        return ResponseEntity.ok(APIResponse.<GradeDashboardResponse>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN]
     * API để giáo viên nhập điểm hoặc sửa điểm cho sinh viên
     */
    @PostMapping("/input")
    public ResponseEntity<APIResponse<GradeResponse>> inputGrade(@RequestBody GradeRequest request) {
        GradeResponse data = gradeService.inputOrUpdateGrade(request);
        return ResponseEntity.ok(APIResponse.<GradeResponse>builder()
                .status(200).code(1000).message("Nhập điểm thành công").data(data).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN]
     * API lấy danh sách học sinh và điểm của một môn trong một lớp
     */
    @GetMapping("/class/{classId}/subject/{subjectId}")
    public ResponseEntity<APIResponse<List<com.fpt.myfschool.dto.response.StudentGradeResponse>>> getStudentGrades(
            @PathVariable Integer classId,
            @PathVariable Integer subjectId,
            @RequestParam(required = false, defaultValue = "1") Integer semesterId) {
        
        List<com.fpt.myfschool.dto.response.StudentGradeResponse> data = gradeService.getStudentGradesByClassAndSubject(classId, subjectId, semesterId);
        return ResponseEntity.ok(APIResponse.<List<com.fpt.myfschool.dto.response.StudentGradeResponse>>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }
}