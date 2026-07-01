package com.fpt.myfschool.service.impl;
import com.fpt.myfschool.dto.request.LeaveReqDto;
import com.fpt.myfschool.dto.response.LeaveResDto;
import com.fpt.myfschool.entity.LeaveRequest;
import com.fpt.myfschool.entity.User;
import com.fpt.myfschool.exception.AppException;
import com.fpt.myfschool.exception.ErrorCode;
import com.fpt.myfschool.mapper.LeaveMapper;
import com.fpt.myfschool.repository.LeaveRequestRepository;
import com.fpt.myfschool.repository.UserRepository;
import com.fpt.myfschool.service.LeaveService;
import com.fpt.myfschool.service.SmartNotificationEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {
    private final LeaveRequestRepository leaveRepo;
    private final UserRepository userRepo;
    private final LeaveMapper leaveMapper;
    private final SmartNotificationEngine notificationEngine;

    /**
     * Nộp Đơn Xin Nghỉ Phép
     * - Tìm kiếm Student theo ID, văng lỗi nếu học sinh không hợp lệ.
     * - Khởi tạo đối tượng LeaveRequest bằng Lombok Builder.
     * - Luôn luôn gán trạng thái là PENDING (Chờ duyệt) khi học sinh vừa nộp. Học sinh không được phép chọn trạng thái này.
     */
    @Override
    public LeaveResDto submitLeaveRequest(LeaveReqDto request) {
        User student = userRepo.findById(request.getStudentId()).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        
        LeaveRequest leave = LeaveRequest.builder()
                .student(student)
                .title(request.getTitle())
                .reason(request.getReason())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(LeaveRequest.LeaveStatus.PENDING)
                .build();
                
        LeaveRequest saved = leaveRepo.save(leave);
        return leaveMapper.toDto(saved);
    }

    /**
     * Xem Lịch Sử Nghỉ Phép
     * - Gọi Repository để lấy ra các đơn nghỉ phép, sắp xếp Ngày tạo giảm dần (Đơn nộp gần nhất sẽ hiển thị trên cùng).
     */
    @Override
    public List<LeaveResDto> getStudentLeaveHistory(Long studentId) {
        return leaveRepo.findByStudentIdOrderByCreatedAtDesc(studentId).stream()
                .map(leaveMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Xem tất cả đơn (Dành cho Giáo viên)
     */
    @Override
    public List<LeaveResDto> getAllLeaves(Integer classId) {
        List<LeaveRequest> requests;
        if (classId != null) {
            requests = leaveRepo.findByClassIdOrderByCreatedAtDesc(classId);
        } else {
            requests = leaveRepo.findAllByOrderByCreatedAtDesc();
        }
        return requests.stream().map(leaveMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Duyệt Đơn (Dành cho Giáo viên)
     * - Kiểm tra đơn có tồn tại không.
     * - Enum LeaveStatus (APPROVED, REJECTED) được truyền từ ngoài vào. Dùng Enum.valueOf() để convert chữ sang Enum.
     */
    @Override
    public LeaveResDto reviewLeaveRequest(Long id, String status) {
        LeaveRequest leave = leaveRepo.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        leave.setStatus(LeaveRequest.LeaveStatus.valueOf(status.toUpperCase()));
        LeaveRequest saved = leaveRepo.save(leave);
        notificationEngine.notifyLeaveReviewed(saved);
        return leaveMapper.toDto(saved);
    }
}