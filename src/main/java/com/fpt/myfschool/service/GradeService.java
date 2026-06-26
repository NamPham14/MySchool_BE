package com.fpt.myfschool.service;
import com.fpt.myfschool.dto.request.GradeRequest;
import com.fpt.myfschool.dto.response.GradeDashboardResponse;
import com.fpt.myfschool.dto.response.GradeResponse;

public interface GradeService {
    GradeDashboardResponse getStudentGradeDashboard(Long studentId, Integer semesterId);
    GradeResponse inputOrUpdateGrade(GradeRequest request);
    void calculateAndSaveAcademicSummary(Long studentId, Integer semesterId);
    java.util.List<com.fpt.myfschool.dto.response.StudentGradeResponse> getStudentGradesByClassAndSubject(Integer classId, Integer subjectId, Integer semesterId);
}
