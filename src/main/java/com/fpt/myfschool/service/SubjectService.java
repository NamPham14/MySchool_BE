package com.fpt.myfschool.service;

import com.fpt.myfschool.dto.request.SubjectRequest;
import com.fpt.myfschool.dto.response.SubjectResponse;
import org.springframework.data.domain.Page;

public interface SubjectService {
    Page<SubjectResponse> getSubjects(String search, int page, int size);
    SubjectResponse createSubject(SubjectRequest request);
    SubjectResponse updateSubject(Integer id, SubjectRequest request);
    void deleteSubject(Integer id);
}
