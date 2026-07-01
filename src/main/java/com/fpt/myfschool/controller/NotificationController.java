package com.fpt.myfschool.controller;
import com.fpt.myfschool.dto.response.APIResponse;
import com.fpt.myfschool.dto.response.NotificationResponse;
import com.fpt.myfschool.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import com.fpt.myfschool.security.UserDetailsImpl;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;
    
    private Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) principal).getId();
        }
        return 1L;
    }

    /**
     * [DÀNH CHO HỌC SINH & GIÁO VIÊN]
     * API để App kéo danh sách thông báo về quả chuông
     */
    @GetMapping
    public ResponseEntity<APIResponse<List<NotificationResponse>>> getMyNotifications() {
        List<NotificationResponse> data = notificationService.getMyNotifications(getCurrentUserId());
        return ResponseEntity.ok(APIResponse.<List<NotificationResponse>>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }

    /**
     * [DÀNH CHO HỌC SINH & GIÁO VIÊN]
     * API để lấy số lượng tin chưa đọc (Hiện chấm đỏ 🔴)
     */
    @GetMapping("/unread-count")
    public ResponseEntity<APIResponse<Long>> getUnreadCount() {
        long data = notificationService.getUnreadCount(getCurrentUserId());
        return ResponseEntity.ok(APIResponse.<Long>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }

    /**
     * [DÀNH CHO HỌC SINH & GIÁO VIÊN]
     * API đánh dấu thông báo đã đọc
     */
    @PutMapping("/{id}/read")
    public ResponseEntity<APIResponse<String>> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok(APIResponse.<String>builder()
                .status(200).code(1000).message("Đã đánh dấu đã đọc").data("OK").build());
    }

    /**
     * [DÀNH CHO HỌC SINH & GIÁO VIÊN]
     * API đánh dấu tất cả thông báo đã đọc
     */
    @PutMapping("/read-all")
    public ResponseEntity<APIResponse<String>> markAllAsRead() {
        notificationService.markAllAsRead(getCurrentUserId());
        return ResponseEntity.ok(APIResponse.<String>builder()
                .status(200).code(1000).message("Đã đánh dấu tất cả đã đọc").data("OK").build());
    }
}