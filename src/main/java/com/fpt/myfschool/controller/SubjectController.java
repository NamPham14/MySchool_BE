package com.fpt.myfschool.controller;
import com.fpt.myfschool.dto.request.SubjectRequest;
import com.fpt.myfschool.dto.response.APIResponse;
import com.fpt.myfschool.dto.response.SubjectResponse;
import com.fpt.myfschool.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subjects")
@RequiredArgsConstructor
public class SubjectController {
    private final SubjectService subjectService;

    /**
     * [DÀNH CHO GIÁO VIÊN & HỌC SINH]
     * API lấy danh sách toàn bộ môn học để tra cứu hoặc lọc
     */
    @GetMapping
    public ResponseEntity<APIResponse<Page<SubjectResponse>>> getSubjects(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<SubjectResponse> data = subjectService.getSubjects(search, page, size);
        return ResponseEntity.ok(APIResponse.<Page<SubjectResponse>>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN]
     * API quản lý: Tạo môn học mới vào hệ thống
     */
    @PostMapping
    public ResponseEntity<APIResponse<SubjectResponse>> createSubject(@RequestBody SubjectRequest request) {
        SubjectResponse data = subjectService.createSubject(request);
        return ResponseEntity.ok(APIResponse.<SubjectResponse>builder()
                .status(200).code(1000).message("Thêm mới thành công").data(data).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN]
     * API quản lý: Cập nhật thông tin mã môn, tên môn
     */
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<SubjectResponse>> updateSubject(@PathVariable Integer id, @RequestBody SubjectRequest request) {
        SubjectResponse data = subjectService.updateSubject(id, request);
        return ResponseEntity.ok(APIResponse.<SubjectResponse>builder()
                .status(200).code(1000).message("Cập nhật thành công").data(data).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN]
     * API quản lý: Xóa môn học khỏi hệ thống
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteSubject(@PathVariable Integer id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.ok(APIResponse.<String>builder()
                .status(200).code(1000).message("Xóa thành công").data("OK").build());
    }
}