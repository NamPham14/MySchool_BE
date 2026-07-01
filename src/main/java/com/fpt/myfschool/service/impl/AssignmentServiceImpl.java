package com.fpt.myfschool.service.impl;
import com.fpt.myfschool.dto.response.AssignmentDto;
import com.fpt.myfschool.entity.Assignment;
import com.fpt.myfschool.exception.AppException;
import com.fpt.myfschool.exception.ErrorCode;
import com.fpt.myfschool.mapper.AssignmentMapper;
import com.fpt.myfschool.repository.AssignmentRepository;
import com.fpt.myfschool.service.AssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentServiceImpl implements AssignmentService {
    private final AssignmentRepository assignmentRepo;
    private final AssignmentMapper assignmentMapper;
    private final com.fpt.myfschool.repository.SchoolClassRepository classRepository;
    private final com.fpt.myfschool.repository.SubjectRepository subjectRepository;
    private final com.fpt.myfschool.repository.UserRepository userRepository;
    private final com.fpt.myfschool.service.SmartNotificationEngine notificationEngine;

    /**
     * Xem Bài Tập Của Lớp
     * - Trả về toàn bộ Nhắc việc (Assignments) được gán cho Lớp đó.
     * - Hệ thống tự động sort theo Ngày hết hạn tăng dần (Bài nào sát deadline nhất sẽ bị đẩy lên top).
     */
    @Override
    public List<AssignmentDto> getAssignmentsByClass(Integer classId) {
        return assignmentRepo.findBySchoolClassIdOrderByDueDateAsc(classId)
                .stream().map(assignmentMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<AssignmentDto> getMyAssignments() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof com.fpt.myfschool.security.UserDetailsImpl) {
            Long currentUserId = ((com.fpt.myfschool.security.UserDetailsImpl) auth.getPrincipal()).getId();
            com.fpt.myfschool.entity.User user = userRepository.findById(currentUserId).orElse(null);
            if (user != null && user.getSchoolClass() != null) {
                return getAssignmentsByClass(user.getSchoolClass().getId());
            }
        }
        return java.util.Collections.emptyList();
    }

    /**
     * Xem Chi Tiết Bài Tập
     * - Lấy ra toàn bộ nội dung của nhắc việc để hiển thị trên màn hình Detail.
     */
    @Override
    public AssignmentDto getAssignmentDetail(Long id) {
        Assignment a = assignmentRepo.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        return assignmentMapper.toDto(a);
    }

    @Override
    public AssignmentDto createAssignment(com.fpt.myfschool.dto.request.AssignmentRequest request) {
        Assignment assignment = assignmentMapper.toEntity(request);
        com.fpt.myfschool.entity.SchoolClass schoolClass = classRepository.findById(request.getClassId()).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        com.fpt.myfschool.entity.Subject subject = subjectRepository.findById(request.getSubjectId()).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        com.fpt.myfschool.entity.User teacher = userRepository.findById(request.getTeacherId()).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        
        assignment.setSchoolClass(schoolClass);
        assignment.setSubject(subject);
        assignment.setTeacher(teacher);
        
        Assignment saved = assignmentRepo.save(assignment);
        
        // Gửi thông báo cho toàn bộ học sinh trong lớp
        List<com.fpt.myfschool.entity.User> students = userRepository.findBySchoolClassId(schoolClass.getId());
        notificationEngine.notifyNewAssignment(saved, students);
        
        return assignmentMapper.toDto(saved);
    }

    @Override
    public AssignmentDto updateAssignment(Long id, com.fpt.myfschool.dto.request.AssignmentRequest request) {
        Assignment assignment = assignmentRepo.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        
        if (request.getClassId() != null) {
            com.fpt.myfschool.entity.SchoolClass schoolClass = classRepository.findById(request.getClassId()).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
            assignment.setSchoolClass(schoolClass);
        }
        if (request.getSubjectId() != null) {
            com.fpt.myfschool.entity.Subject subject = subjectRepository.findById(request.getSubjectId()).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
            assignment.setSubject(subject);
        }
        if (request.getTeacherId() != null) {
            com.fpt.myfschool.entity.User teacher = userRepository.findById(request.getTeacherId()).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
            assignment.setTeacher(teacher);
        }
        
        assignment.setTitle(request.getTitle());
        assignment.setDescription(request.getDescription());
        assignment.setImageUrl(request.getImageUrl());
        assignment.setDueDate(request.getDueDate());
        
        return assignmentMapper.toDto(assignmentRepo.save(assignment));
    }

    @Override
    public void deleteAssignment(Long id) {
        assignmentRepo.deleteById(id);
    }
}