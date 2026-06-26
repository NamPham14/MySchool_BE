package com.fpt.myfschool.service;
import com.fpt.myfschool.dto.request.LeaveReqDto;
import com.fpt.myfschool.dto.response.LeaveResDto;
import java.util.List;

public interface LeaveService {
    LeaveResDto submitLeaveRequest(LeaveReqDto request);
    List<LeaveResDto> getStudentLeaveHistory(Long studentId);
    List<LeaveResDto> getAllLeaves(Integer classId);
    LeaveResDto reviewLeaveRequest(Long id, String status);
}