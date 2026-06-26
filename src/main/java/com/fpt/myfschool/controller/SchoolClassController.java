package com.fpt.myfschool.controller;
import com.fpt.myfschool.dto.request.SchoolClassRequest;
import com.fpt.myfschool.dto.response.APIResponse;
import com.fpt.myfschool.dto.response.SchoolClassResponse;
import com.fpt.myfschool.service.SchoolClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
public class SchoolClassController {
    private final SchoolClassService classService;

    /**
     * [DÀNH CHO GIÁO VIÊN & HỌC SINH]
     * Xem danh sách lớp học
     */
    @GetMapping
    public ResponseEntity<APIResponse<Page<SchoolClassResponse>>> getClasses(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<SchoolClassResponse> data = classService.getClasses(search, page, size);
        return ResponseEntity.ok(APIResponse.<Page<SchoolClassResponse>>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN]
     * API quản lý: Tạo một lớp học mới
     */
    @PostMapping
    public ResponseEntity<APIResponse<SchoolClassResponse>> createClass(@RequestBody SchoolClassRequest request) {
        SchoolClassResponse data = classService.createClass(request);
        return ResponseEntity.ok(APIResponse.<SchoolClassResponse>builder()
                .status(200).code(1000).message("Thêm mới thành công").data(data).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN]
     * API quản lý: Sửa thông tin lớp (Đổi khối, đổi niên khóa)
     */
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<SchoolClassResponse>> updateClass(@PathVariable Integer id, @RequestBody SchoolClassRequest request) {
        SchoolClassResponse data = classService.updateClass(id, request);
        return ResponseEntity.ok(APIResponse.<SchoolClassResponse>builder()
                .status(200).code(1000).message("Cập nhật thành công").data(data).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN]
     * API quản lý: Xóa lớp học
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteClass(@PathVariable Integer id) {
        classService.deleteClass(id);
        return ResponseEntity.ok(APIResponse.<String>builder()
                .status(200).code(1000).message("Xóa thành công").data("OK").build());
    }
}