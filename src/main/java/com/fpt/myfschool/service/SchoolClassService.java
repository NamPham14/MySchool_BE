package com.fpt.myfschool.service;
import com.fpt.myfschool.dto.request.SchoolClassRequest;
import com.fpt.myfschool.dto.response.SchoolClassResponse;
import org.springframework.data.domain.Page;

public interface SchoolClassService {
    Page<SchoolClassResponse> getClasses(String search, int page, int size);
    SchoolClassResponse createClass(SchoolClassRequest request);
    SchoolClassResponse updateClass(Integer id, SchoolClassRequest request);
    void deleteClass(Integer id);
}