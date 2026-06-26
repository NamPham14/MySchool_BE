package com.fpt.myfschool.service.impl;
import com.fpt.myfschool.dto.request.SemesterRequest;
import com.fpt.myfschool.dto.response.SemesterResponse;
import com.fpt.myfschool.entity.Semester;
import com.fpt.myfschool.exception.AppException;
import com.fpt.myfschool.exception.ErrorCode;
import com.fpt.myfschool.mapper.SemesterMapper;
import com.fpt.myfschool.repository.SemesterRepository;
import com.fpt.myfschool.service.SemesterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SemesterServiceImpl implements SemesterService {
    private final SemesterRepository semesterRepository;
    private final SemesterMapper semesterMapper;

    @Override
    public Page<SemesterResponse> getSemesters(String search, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Semester> semesters = (search != null && !search.isEmpty()) ?
                semesterRepository.findByNameContainingIgnoreCase(search, pageable) :
                semesterRepository.findAll(pageable);
        return semesters.map(semesterMapper::toDto);
    }

    @Override
    public SemesterResponse createSemester(SemesterRequest request) {
        Semester entity = semesterMapper.toEntity(request);
        return semesterMapper.toDto(semesterRepository.save(entity));
    }

    @Override
    public SemesterResponse updateSemester(Integer id, SemesterRequest request) {
        Semester entity = semesterRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        entity.setName(request.getName());
        entity.setStartDate(request.getStartDate());
        entity.setEndDate(request.getEndDate());
        return semesterMapper.toDto(semesterRepository.save(entity));
    }

    @Override
    public void deleteSemester(Integer id) {
        semesterRepository.deleteById(id);
    }
}