package com.fpt.myfschool.service;
import com.fpt.myfschool.dto.request.GradeRequest;
import com.fpt.myfschool.dto.response.GradeDashboardResponse;
import com.fpt.myfschool.dto.response.GradeResponse;
import com.fpt.myfschool.dto.response.StudentGradeResponse;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

public interface GradeService {
    GradeDashboardResponse getStudentGradeDashboard(Long studentId, Integer semesterId);
    GradeResponse inputOrUpdateGrade(GradeRequest request);
    void calculateAndSaveAcademicSummary(Long studentId, Integer semesterId);
    List<StudentGradeResponse> getStudentGradesByClassAndSubject(Integer classId, Integer subjectId, Integer semesterId);

    ByteArrayInputStream exportGradeTemplate(Integer classId);
    void importGradesFromExcel(MultipartFile file, Integer subjectId, Integer semesterId);

}
