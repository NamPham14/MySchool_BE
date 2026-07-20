package com.fpt.myfschool.controller;

import com.fpt.myfschool.dto.request.ClubRequest;
import com.fpt.myfschool.dto.response.APIResponse;
import com.fpt.myfschool.dto.response.ClubMemberResponse;
import com.fpt.myfschool.dto.response.ClubResponse;
import com.fpt.myfschool.security.UserDetailsImpl;
import com.fpt.myfschool.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clubs")
@RequiredArgsConstructor
public class ClubController {
    private final ClubService clubService;

    private Long getCurrentUserId() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }

    // CLUBS CRUD
    @GetMapping
    public ResponseEntity<APIResponse<List<ClubResponse>>> getAllClubs() {
        return ResponseEntity.ok(APIResponse.<List<ClubResponse>>builder()
                .status(200).code(1000).message("Thành công").data(clubService.getAllClubs(getCurrentUserId())).build());
    }

    @GetMapping("/active")
    public ResponseEntity<APIResponse<List<ClubResponse>>> getActiveClubs() {
        return ResponseEntity.ok(APIResponse.<List<ClubResponse>>builder()
                .status(200).code(1000).message("Thành công").data(clubService.getActiveClubs(getCurrentUserId())).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<ClubResponse>> getClubById(@PathVariable Long id) {
        return ResponseEntity.ok(APIResponse.<ClubResponse>builder()
                .status(200).code(1000).message("Thành công").data(clubService.getClubById(id, getCurrentUserId())).build());
    }

    @PostMapping
    public ResponseEntity<APIResponse<ClubResponse>> createClub(@RequestBody ClubRequest request) {
        return ResponseEntity.ok(APIResponse.<ClubResponse>builder()
                .status(200).code(1000).message("Tạo Câu lạc bộ thành công").data(clubService.createClub(request)).build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<ClubResponse>> updateClub(@PathVariable Long id, @RequestBody ClubRequest request) {
        return ResponseEntity.ok(APIResponse.<ClubResponse>builder()
                .status(200).code(1000).message("Cập nhật Câu lạc bộ thành công").data(clubService.updateClub(id, request)).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<String>> deleteClub(@PathVariable Long id) {
        clubService.deleteClub(id);
        return ResponseEntity.ok(APIResponse.<String>builder()
                .status(200).code(1000).message("Xóa Câu lạc bộ thành công").data("OK").build());
    }

    // MEMBERSHIP
    @GetMapping("/{clubId}/members")
    public ResponseEntity<APIResponse<List<ClubMemberResponse>>> getClubMembers(@PathVariable Long clubId) {
        return ResponseEntity.ok(APIResponse.<List<ClubMemberResponse>>builder()
                .status(200).code(1000).message("Thành công").data(clubService.getClubMembers(clubId)).build());
    }

    @PostMapping("/{clubId}/join")
    public ResponseEntity<APIResponse<ClubMemberResponse>> joinClub(@PathVariable Long clubId) {
        return ResponseEntity.ok(APIResponse.<ClubMemberResponse>builder()
                .status(200).code(1000).message("Gửi yêu cầu tham gia thành công, vui lòng chờ duyệt").data(clubService.joinClub(clubId, getCurrentUserId())).build());
    }

    @PostMapping("/members/{memberId}/approve")
    public ResponseEntity<APIResponse<String>> approveMember(@PathVariable Long memberId) {
        clubService.approveMember(memberId);
        return ResponseEntity.ok(APIResponse.<String>builder()
                .status(200).code(1000).message("Đã duyệt thành viên").data("OK").build());
    }

    @PostMapping("/members/{memberId}/reject")
    public ResponseEntity<APIResponse<String>> rejectMember(@PathVariable Long memberId) {
        clubService.rejectMember(memberId);
        return ResponseEntity.ok(APIResponse.<String>builder()
                .status(200).code(1000).message("Đã từ chối thành viên").data("OK").build());
    }

    @DeleteMapping("/{clubId}/leave")
    public ResponseEntity<APIResponse<String>> leaveClub(@PathVariable Long clubId) {
        clubService.leaveClub(clubId, getCurrentUserId());
        return ResponseEntity.ok(APIResponse.<String>builder()
                .status(200).code(1000).message("Đã rời Câu lạc bộ").data("OK").build());
    }
}
