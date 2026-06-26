package com.fpt.myfschool.service;
import com.fpt.myfschool.dto.request.AssignmentRequest;
import com.fpt.myfschool.dto.response.AssignmentDto;
import java.util.List;

public interface AssignmentService {
    List<AssignmentDto> getAssignmentsByClass(Integer classId);
    List<AssignmentDto> getMyAssignments();
    AssignmentDto getAssignmentDetail(Long id);
    AssignmentDto createAssignment(AssignmentRequest request);
    AssignmentDto updateAssignment(Long id, AssignmentRequest request);
    void deleteAssignment(Long id);
}