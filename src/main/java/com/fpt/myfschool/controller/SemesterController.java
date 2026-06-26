package com.fpt.myfschool.controller;
import com.fpt.myfschool.dto.request.SemesterRequest;
import com.fpt.myfschool.dto.response.APIResponse;
import com.fpt.myfschool.dto.response.SemesterResponse;
import com.fpt.myfschool.service.SemesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/semesters")
@RequiredArgsConstructor
public class SemesterController {
    private final SemesterService semesterService;

    /**
     * [DÀNH CHO BAN GIÁM HIỆU / ADMIN]
     * API hiển thị danh sách tất cả học kỳ
     */
    @GetMapping
    public ResponseEntity<APIResponse<Page<SemesterResponse>>> getSemesters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(APIResponse.<Page<SemesterResponse>>builder()
                .status(HttpStatus.OK.value()).code(1000).message("Thành công")
                .data(semesterService.getSemesters(search, page, size)).build());
    }

    /**
     * [DÀNH CHO BAN GIÁM HIỆU / ADMIN]
     * API tạo học kỳ mới
     */
    @PostMapping
    public ResponseEntity<APIResponse<SemesterResponse>> createSemester(@RequestBody SemesterRequest request) {
        return ResponseEntity.ok(APIResponse.<SemesterResponse>builder()
                .status(HttpStatus.OK.value()).code(1000).message("Thành công")
                .data(semesterService.createSemester(request)).build());
    }

    /**
     * [DÀNH CHO BAN GIÁM HIỆU / ADMIN]
     * API cập nhật thông tin học kỳ
     */
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<SemesterResponse>> updateSemester(@PathVariable Integer id, @RequestBody SemesterRequest request) {
        return ResponseEntity.ok(APIResponse.<SemesterResponse>builder()
                .status(HttpStatus.OK.value()).code(1000).message("Thành công")
                .data(semesterService.updateSemester(id, request)).build());
    }

    /**
     * [DÀNH CHO BAN GIÁM HIỆU / ADMIN]
     * API xóa học kỳ
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteSemester(@PathVariable Integer id) {
        semesterService.deleteSemester(id);
        return ResponseEntity.ok(APIResponse.<Void>builder()
                .status(HttpStatus.OK.value()).code(1000).message("Thành công").build());
    }
}