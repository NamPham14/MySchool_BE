package com.fpt.myfschool.controller;

import com.fpt.myfschool.dto.request.AnnouncementRequest;
import com.fpt.myfschool.dto.response.APIResponse;
import com.fpt.myfschool.dto.response.AnnouncementResponse;
import com.fpt.myfschool.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;

    @PostMapping
    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    public ResponseEntity<APIResponse<AnnouncementResponse>> createAnnouncement(@RequestBody AnnouncementRequest request) {
        AnnouncementResponse data = announcementService.createAnnouncement(request);
        return ResponseEntity.ok(APIResponse.<AnnouncementResponse>builder()
                .status(200).code(1000).message("Đăng thông báo thành công").data(data).build());
    }

    @GetMapping("/class/{classId}")
    public ResponseEntity<APIResponse<List<AnnouncementResponse>>> getAnnouncementsByClass(@PathVariable Integer classId) {
        List<AnnouncementResponse> data = announcementService.getAnnouncementsByClass(classId);
        return ResponseEntity.ok(APIResponse.<List<AnnouncementResponse>>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }

    @GetMapping("/my-class")
    public ResponseEntity<APIResponse<List<AnnouncementResponse>>> getMyAnnouncements() {
        List<AnnouncementResponse> data = announcementService.getMyAnnouncements();
        return ResponseEntity.ok(APIResponse.<List<AnnouncementResponse>>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    public ResponseEntity<APIResponse<String>> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.ok(APIResponse.<String>builder()
                .status(200).code(1000).message("Xóa thông báo thành công").build());
    }
}
