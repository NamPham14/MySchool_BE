package com.fpt.myfschool.controller;
import com.fpt.myfschool.dto.response.APIResponse;
import com.fpt.myfschool.dto.response.AssignmentDto;
import com.fpt.myfschool.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
public class AssignmentController {
    private final AssignmentService assignmentService;

    /**
     * [DÀNH CHO HỌC SINH]
     * API trên màn hình App để học sinh xem các bài tập Giáo viên đã giao cho lớp
     */
    @GetMapping("/class/{classId}")
    public ResponseEntity<APIResponse<List<AssignmentDto>>> getAssignments(@PathVariable Integer classId) {
        List<AssignmentDto> data = assignmentService.getAssignmentsByClass(classId);
        return ResponseEntity.ok(APIResponse.<List<AssignmentDto>>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }

    /**
     * [DÀNH CHO HỌC SINH]
     * API xem danh sách bài tập của cá nhân (tạm thời hardcode class 1)
     */
    @GetMapping("/student")
    public ResponseEntity<APIResponse<List<AssignmentDto>>> getMyAssignments() {
        List<AssignmentDto> data = assignmentService.getMyAssignments();
        return ResponseEntity.ok(APIResponse.<List<AssignmentDto>>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }

    /**
     * [DÀNH CHO HỌC SINH & GIÁO VIÊN]
     * API xem chi tiết nội dung bài tập, hạn nộp
     */
    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<AssignmentDto>> getAssignmentDetail(@PathVariable Long id) {
        AssignmentDto data = assignmentService.getAssignmentDetail(id);
        return ResponseEntity.ok(APIResponse.<AssignmentDto>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN]
     * API thêm bài tập mới
     */
    @PostMapping
    public ResponseEntity<APIResponse<AssignmentDto>> createAssignment(@RequestBody com.fpt.myfschool.dto.request.AssignmentRequest request) {
        AssignmentDto data = assignmentService.createAssignment(request);
        return ResponseEntity.ok(APIResponse.<AssignmentDto>builder()
                .status(200).code(1000).message("Thêm bài tập thành công").data(data).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN]
     * API cập nhật bài tập
     */
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<AssignmentDto>> updateAssignment(@PathVariable Long id, @RequestBody com.fpt.myfschool.dto.request.AssignmentRequest request) {
        AssignmentDto data = assignmentService.updateAssignment(id, request);
        return ResponseEntity.ok(APIResponse.<AssignmentDto>builder()
                .status(200).code(1000).message("Cập nhật bài tập thành công").data(data).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN]
     * API xóa bài tập
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteAssignment(@PathVariable Long id) {
        assignmentService.deleteAssignment(id);
        return ResponseEntity.ok(APIResponse.<String>builder()
                .status(200).code(1000).message("Xóa bài tập thành công").data("OK").build());
    }
}