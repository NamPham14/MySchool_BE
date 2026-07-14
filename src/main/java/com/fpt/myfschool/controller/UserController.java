package com.fpt.myfschool.controller;
import com.fpt.myfschool.dto.request.UpdateProfileRequest;
import com.fpt.myfschool.dto.response.APIResponse;
import com.fpt.myfschool.dto.response.UserProfileResponse;
import com.fpt.myfschool.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.context.SecurityContextHolder;
import com.fpt.myfschool.security.UserDetailsImpl;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    
    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) principal).getId();
        }
        throw new RuntimeException("Unauthorized: User not found or token expired");
    }

    /**
     * [DÀNH CHO HỌC SINH & GIÁO VIÊN]
     * API hiển thị thông tin cá nhân trên tab Profile
     */
    @GetMapping("/profile")
    public ResponseEntity<APIResponse<UserProfileResponse>> getMyProfile() {
        UserProfileResponse data = userService.getMyProfile(getCurrentUserId());
        return ResponseEntity.ok(APIResponse.<UserProfileResponse>builder()
                .status(200).code(1000).message("Lấy thông tin thành công").data(data).build());
    }

    /**
     * [DÀNH CHO HỌC SINH & GIÁO VIÊN]
     * API cho phép người dùng tự đổi Avatar, SĐT, Tên hiển thị
     */
    @PutMapping("/profile")
    public ResponseEntity<APIResponse<UserProfileResponse>> updateMyProfile(@RequestBody UpdateProfileRequest request) {
        UserProfileResponse data = userService.updateMyProfile(getCurrentUserId(), request);
        return ResponseEntity.ok(APIResponse.<UserProfileResponse>builder()
                .status(200).code(1000).message("Cập nhật thông tin thành công").data(data).build());
    }

    /**
     * [DÀNH CHO HỌC SINH & GIÁO VIÊN]
     * API cho phép người dùng tự đổi Ảnh đại diện (Avatar) tải lên từ máy
     */
    @PostMapping("/profile/avatar")
    public ResponseEntity<APIResponse<String>> uploadAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        String avatarUrl = userService.uploadAvatar(getCurrentUserId(), file);
        return ResponseEntity.ok(APIResponse.<String>builder()
                .status(200).code(1000).message("Cập nhật ảnh đại diện thành công").data(avatarUrl).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN/ADMIN]
     * API lấy danh sách học sinh (có thể lọc theo lớp, hoặc không thì lấy tất cả)
     */
    @GetMapping("/students")
    public ResponseEntity<APIResponse<java.util.List<UserProfileResponse>>> getStudents(
            @RequestParam(required = false) Integer classId) {
        java.util.List<UserProfileResponse> data;
        if (classId != null) {
            data = userService.getStudentsByClass(classId);
        } else {
            data = userService.getAllStudents();
        }
        return ResponseEntity.ok(APIResponse.<java.util.List<UserProfileResponse>>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN/ADMIN]
     * API lấy danh sách giáo viên
     */
    @GetMapping("/teachers")
    public ResponseEntity<APIResponse<java.util.List<UserProfileResponse>>> getTeachers() {
        java.util.List<UserProfileResponse> data = userService.getAllTeachers();
        return ResponseEntity.ok(APIResponse.<java.util.List<UserProfileResponse>>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN/ADMIN]
     * API gán học sinh vào một lớp
     */
    @PutMapping("/students/{studentId}/assign")
    public ResponseEntity<APIResponse<Void>> assignStudentToClass(
            @PathVariable Long studentId,
            @RequestParam Integer classId) {
        userService.assignStudentToClass(studentId, classId);
        return ResponseEntity.ok(APIResponse.<Void>builder()
                .status(200).code(1000).message("Gán học sinh vào lớp thành công").build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN/ADMIN]
     * API tạo mới một học sinh và gán vào lớp
     */
    @PostMapping("/students")
    public ResponseEntity<APIResponse<UserProfileResponse>> createStudent(
            @RequestBody com.fpt.myfschool.dto.request.RegisterRequest request, 
            @RequestParam Integer classId) {
        UserProfileResponse data = userService.createStudent(request, classId);
        return ResponseEntity.ok(APIResponse.<UserProfileResponse>builder()
                .status(200).code(1000).message("Tạo học sinh thành công").data(data).build());
    }
}