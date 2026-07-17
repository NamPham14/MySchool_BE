package com.fpt.myfschool.controller;
import com.fpt.myfschool.dto.request.LeaveReqDto;
import com.fpt.myfschool.dto.response.APIResponse;
import com.fpt.myfschool.dto.response.LeaveResDto;
import com.fpt.myfschool.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import com.fpt.myfschool.security.UserDetailsImpl;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {
    private final LeaveService leaveService;

    private Long getCurrentUserId() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }

    /**
     * [DÀNH CHO HỌC SINH]
     * API trên màn hình Học sinh thao tác để nộp đơn xin nghỉ phép
     */
    @PostMapping("/submit")
    public ResponseEntity<APIResponse<LeaveResDto>> submitLeave(@RequestBody LeaveReqDto request, @RequestParam(required = false) Long studentId) {
        Long sId = (studentId != null) ? studentId : getCurrentUserId();
        request.setStudentId(sId);
        LeaveResDto data = leaveService.submitLeaveRequest(request);
        return ResponseEntity.ok(APIResponse.<LeaveResDto>builder()
                .status(200).code(1000).message("Nộp đơn thành công").data(data).build());
    }

    /**
     * [DÀNH CHO HỌC SINH]
     * API trên màn hình Học sinh thao tác để xem lịch sử nghỉ phép của mình
     */
    @GetMapping("/my-requests")
    public ResponseEntity<APIResponse<List<LeaveResDto>>> getMyHistory(@RequestParam(required = false) Long studentId) {
        Long sId = (studentId != null) ? studentId : getCurrentUserId();
        List<LeaveResDto> data = leaveService.getStudentLeaveHistory(sId);
        return ResponseEntity.ok(APIResponse.<List<LeaveResDto>>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN]
     * API để Giáo viên xem danh sách tất cả đơn từ của học sinh
     */
    @GetMapping
    public ResponseEntity<APIResponse<List<LeaveResDto>>> getAllLeaves(@RequestParam(required = false) Integer classId) {
        List<LeaveResDto> data = leaveService.getAllLeaves(classId);
        return ResponseEntity.ok(APIResponse.<List<LeaveResDto>>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN]
     * API để Giáo viên quản lý đơn, tiến hành duyệt (APPROVE) hoặc từ chối (REJECT)
     */
    @PutMapping("/{id}/review")
    public ResponseEntity<APIResponse<LeaveResDto>> reviewLeave(@PathVariable Long id, @RequestParam String status) {
        LeaveResDto data = leaveService.reviewLeaveRequest(id, status);
        return ResponseEntity.ok(APIResponse.<LeaveResDto>builder()
                .status(200).code(1000).message("Duyệt đơn thành công").data(data).build());
    }
}