package com.fpt.myfschool.controller;
import com.fpt.myfschool.dto.request.TimetableRequest;
import com.fpt.myfschool.dto.response.APIResponse;
import com.fpt.myfschool.dto.response.TimetableResponse;
import com.fpt.myfschool.security.UserDetailsImpl;
import com.fpt.myfschool.service.ScheduleService;
import com.fpt.myfschool.entity.User;
import com.fpt.myfschool.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import java.util.Arrays;
import java.util.List;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;
    private final UserRepository userRepository;

    private Long getCurrentUserId() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }

    /**
     * [DÀNH CHO HỌC SINH & GIÁO VIÊN]
     * API trên màn hình hiển thị Thời khóa biểu của Lớp
     */
    @GetMapping("/class/{classId}")
    public ResponseEntity<APIResponse<List<TimetableResponse>>> getSchedules(@PathVariable Integer classId) {
        List<TimetableResponse> data = scheduleService.getSchedulesByClass(classId);
        return ResponseEntity.ok(APIResponse.<List<TimetableResponse>>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }

    /**
     * [DÀNH CHO HỌC SINH]
     * Lấy TKB của chính tôi
     */
    @GetMapping("/my-schedule")
    public ResponseEntity<APIResponse<List<TimetableResponse>>> getMySchedule(
            @RequestParam(required = false) Long studentId) {
        Long sId = (studentId != null) ? studentId : getCurrentUserId();
        User user = userRepository.findById(sId).orElse(null);
        Integer classId = (user != null && user.getSchoolClass() != null) ? user.getSchoolClass().getId() : null;
        
        List<TimetableResponse> data = java.util.Collections.emptyList();
        if (classId != null) {
            data = scheduleService.getSchedulesByClass(classId);
        }

        return ResponseEntity.ok(APIResponse.<List<TimetableResponse>>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }

    /**
     * [DÀNH CHO HỌC SINH & GIÁO VIÊN]
     * API hiển thị Tiết học tiếp theo trên trang chủ App
     */
    @GetMapping("/class/{classId}/next")
    public ResponseEntity<APIResponse<TimetableResponse>> getNextClass(@PathVariable Integer classId) {
        TimetableResponse data = scheduleService.getNextClass(classId);
        return ResponseEntity.ok(APIResponse.<TimetableResponse>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN]
     * API quản lý TKB: Giáo viên xếp tiết học mới cho lớp
     */
    @PostMapping
    public ResponseEntity<APIResponse<TimetableResponse>> createSchedule(@RequestBody TimetableRequest request) {
        TimetableResponse data = scheduleService.createSchedule(request);
        return ResponseEntity.ok(APIResponse.<TimetableResponse>builder()
                .status(200).code(1000).message("Xếp lịch thành công").data(data).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN]
     * API quản lý TKB: Giáo viên sửa giờ học, phòng học
     */
    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<TimetableResponse>> updateSchedule(@PathVariable Long id, @RequestBody TimetableRequest request) {
        TimetableResponse data = scheduleService.updateSchedule(id, request);
        return ResponseEntity.ok(APIResponse.<TimetableResponse>builder()
                .status(200).code(1000).message("Sửa lịch thành công").data(data).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN]
     * API quản lý TKB: Giáo viên xóa tiết học
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok(APIResponse.<String>builder()
                .status(200).code(1000).message("Xóa lịch thành công").data("OK").build());
    }

    /**
     * [DÀNH CHO ADMIN]
     * Tải file Excel mẫu để nhập thời khóa biểu
     */
    @GetMapping("/export/template")
    public ResponseEntity<Resource> exportTemplate() {
        java.io.ByteArrayInputStream in = scheduleService.exportTemplate();
        InputStreamResource file = new InputStreamResource(in);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "Timetable_Template.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }

    /**
     * [DÀNH CHO ADMIN]
     * Nhập thời khóa biểu từ file Excel
     */
    @PostMapping("/import")
    public ResponseEntity<APIResponse<List<TimetableResponse>>> importExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "overwrite", defaultValue = "false") boolean overwrite) {
        
        if (!com.fpt.myfschool.util.TimetableExcelHelper.hasExcelFormat(file)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                APIResponse.<List<TimetableResponse>>builder().status(400).code(4000).message("Định dạng file không hợp lệ. Vui lòng tải lên file Excel (.xlsx)").build()
            );
        }

        List<TimetableResponse> data = scheduleService.importExcel(file, overwrite);
        return ResponseEntity.ok(APIResponse.<List<TimetableResponse>>builder()
                .status(200).code(1000).message("Nhập thời khóa biểu thành công (" + data.size() + " tiết học)").data(data).build());
    }
}