package com.fpt.myfschool.service.impl;

import com.fpt.myfschool.exception.AppException;
import com.fpt.myfschool.exception.ErrorCode;
import com.fpt.myfschool.dto.request.GradeRequest;
import com.fpt.myfschool.dto.response.AcademicSummaryResponse;
import com.fpt.myfschool.dto.response.GradeDashboardResponse;
import com.fpt.myfschool.dto.response.GradeResponse;
import com.fpt.myfschool.entity.AcademicSummary;
import com.fpt.myfschool.entity.Grade;
import com.fpt.myfschool.entity.Semester;
import com.fpt.myfschool.entity.Subject;
import com.fpt.myfschool.entity.User;
import com.fpt.myfschool.mapper.AcademicSummaryMapper;
import com.fpt.myfschool.mapper.GradeMapper;
import com.fpt.myfschool.repository.AcademicSummaryRepository;
import com.fpt.myfschool.repository.GradeRepository;
import com.fpt.myfschool.repository.SemesterRepository;
import com.fpt.myfschool.repository.SubjectRepository;
import com.fpt.myfschool.repository.UserRepository;
import com.fpt.myfschool.service.GradeService;
import com.fpt.myfschool.service.SmartNotificationEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;
    private final AcademicSummaryRepository summaryRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final SemesterRepository semesterRepository;
    private final GradeMapper gradeMapper;
    private final AcademicSummaryMapper summaryMapper;
    private final SmartNotificationEngine notificationEngine;

    /**
     * Lấy Bảng Điểm Tổng Hợp của Học Sinh
     * - Trả về 2 phần: Bảng điểm chi tiết từng môn (details) và Tổng kết chung GPA/Hạnh kiểm (summary).
     * - Cấu trúc này giúp App Mobile chỉ cần gọi 1 API là có đủ dữ liệu để vẽ Dashboard.
     */
    @Override
    public GradeDashboardResponse getStudentGradeDashboard(Long studentId, Integer semesterId) {
        List<Grade> grades = gradeRepository.findByStudentIdAndSemesterId(studentId, semesterId);
        
        // Lấy tất cả môn học trong hệ thống
        List<Subject> allSubjects = subjectRepository.findAll();
        
        // Map điểm với các môn học
        List<GradeResponse> details = allSubjects.stream().map(subject -> {
            GradeResponse response = new GradeResponse();
            response.setSubjectName(subject.getName());
            
            Grade grade = grades.stream()
                    .filter(g -> g.getSubject().getId().equals(subject.getId()))
                    .findFirst()
                    .orElse(null);
                    
            if (grade != null) {
                response.setId(grade.getId());
                response.setMidtermScore(grade.getMidtermScore());
                response.setFinalScore(grade.getFinalScore());
                response.setAverageScore(grade.getAverageScore());
            }
            
            return response;
        }).collect(Collectors.toList());

        AcademicSummary summary = summaryRepository.findByStudentIdAndSemesterId(studentId, semesterId).orElse(null);
        if (summary == null && !grades.isEmpty()) {
            calculateAndSaveAcademicSummary(studentId, semesterId);
            summary = summaryRepository.findByStudentIdAndSemesterId(studentId, semesterId).orElse(null);
        }
        AcademicSummaryResponse summaryResponse = summary != null ? summaryMapper.toResponse(summary) : null;

        return new GradeDashboardResponse(summaryResponse, details);
    }

    /**
     * Nhập Hoặc Sửa Điểm Của Học Sinh
     * - Hệ thống kiểm tra xem điểm môn này đã từng được nhập chưa.
     * - Nếu chưa, tạo bản ghi điểm mới. Nếu có rồi, ghi đè điểm cũ.
     * - Nếu có cả điểm Giữa kỳ (40%) và Cuối kỳ (60%), tự động tính điểm Trung bình môn.
     * - Cuối cùng, kích hoạt tính năng tự động tính lại tổng GPA toàn bộ học kỳ cho học sinh đó (trigger).
     */
    @Override
    @Transactional
    public GradeResponse inputOrUpdateGrade(GradeRequest request) {
        Grade grade = gradeRepository.findByStudentIdAndSubjectIdAndSemesterId(
                request.getStudentId(), request.getSubjectId(), request.getSemesterId()
        ).orElse(new Grade());

        if (grade.getId() == null) {
            User student = userRepository.findById(request.getStudentId()).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
            Subject subject = subjectRepository.findById(request.getSubjectId()).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
            Semester semester = semesterRepository.findById(request.getSemesterId()).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
            grade.setStudent(student);
            grade.setSubject(subject);
            grade.setSemester(semester);
        }

        grade.setMidtermScore(request.getMidtermScore());
        grade.setFinalScore(request.getFinalScore());
        
        if (grade.getMidtermScore() != null && grade.getFinalScore() != null) {
            double avg = (grade.getMidtermScore() * 0.4) + (grade.getFinalScore() * 0.6);
            grade.setAverageScore((double) Math.round(avg * 10) / 10.0);
        }

        Grade saved = gradeRepository.save(grade);
        
        // Tự động kích hoạt tính năng tính tổng GPA
        calculateAndSaveAcademicSummary(request.getStudentId(), request.getSemesterId());
        
        notificationEngine.notifyNewGrade(saved);
        
        return gradeMapper.toResponse(saved);
    }

    /**
     * Tự Động Tính Tổng Kết GPA Của Học Kỳ
     * - Quét tất cả điểm của học sinh trong học kỳ đó.
     * - Cộng dồn trung bình các môn lại và chia đều để ra điểm GPA.
     * - Tự động xếp loại: >= 8.0 (Giỏi), >= 6.5 (Khá), >= 5.0 (Trung Bình), < 5.0 (Yếu).
     */
    @Override
    @Transactional
    public void calculateAndSaveAcademicSummary(Long studentId, Integer semesterId) {
        List<Grade> grades = gradeRepository.findByStudentIdAndSemesterId(studentId, semesterId);
        if (grades.isEmpty()) return;

        double totalScore = 0.0;
        int count = 0;
        for (Grade g : grades) {
            if (g.getAverageScore() != null) {
                totalScore += g.getAverageScore();
                count++;
            }
        }

        if (count > 0) {
            double gpa = (double) Math.round((totalScore / count) * 10) / 10.0;
            String performance = gpa >= 8.0 ? "Giỏi" : (gpa >= 6.5 ? "Khá" : (gpa >= 5.0 ? "Trung Bình" : "Yếu"));
            
            AcademicSummary summary = summaryRepository.findByStudentIdAndSemesterId(studentId, semesterId).orElse(new AcademicSummary());
            if (summary.getId() == null) {
                User student = userRepository.findById(studentId).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
                Semester semester = semesterRepository.findById(semesterId).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
                summary.setStudent(student);
                summary.setSemester(semester);
                summary.setConduct("Tốt"); // Mặc định hạnh kiểm là Tốt
            }
            summary.setGpa(gpa);
            summary.setAcademicPerformance(performance);
            summaryRepository.save(summary);
        }
    }

    /**
     * [DÀNH CHO GIÁO VIÊN]
     * Lấy danh sách học sinh của một lớp, kèm theo điểm của môn học đó.
     */
    @Override
    public List<com.fpt.myfschool.dto.response.StudentGradeResponse> getStudentGradesByClassAndSubject(Integer classId, Integer subjectId, Integer semesterId) {
        // Lấy danh sách học sinh trong lớp (tạm lấy theo class_id vừa thêm)
        List<User> students = userRepository.findBySchoolClassId(classId);
        
        return students.stream().map(student -> {
            com.fpt.myfschool.dto.response.StudentGradeResponse response = new com.fpt.myfschool.dto.response.StudentGradeResponse();
            response.setStudentId(student.getId());
            response.setStudentName(student.getFullName());
            response.setRollNumber(student.getRollNumber());
            response.setAvatarUrl(student.getAvatarUrl());
            
            Grade grade = gradeRepository.findByStudentIdAndSubjectIdAndSemesterId(student.getId(), subjectId, semesterId).orElse(null);
            if (grade != null) {
                response.setGradeId(grade.getId());
                response.setMidtermScore(grade.getMidtermScore());
                response.setFinalScore(grade.getFinalScore());
                response.setAverageScore(grade.getAverageScore());
            }
            
            return response;
        }).collect(Collectors.toList());
    }
}