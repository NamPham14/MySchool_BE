package com.fpt.myfschool.service;
import com.fpt.myfschool.dto.request.SemesterRequest;
import com.fpt.myfschool.dto.response.SemesterResponse;
import org.springframework.data.domain.Page;

public interface SemesterService {
    Page<SemesterResponse> getSemesters(String search, int page, int size);
    SemesterResponse createSemester(SemesterRequest request);
    SemesterResponse updateSemester(Integer id, SemesterRequest request);
    void deleteSemester(Integer id);
}