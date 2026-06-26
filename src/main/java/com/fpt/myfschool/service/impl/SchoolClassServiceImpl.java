package com.fpt.myfschool.service.impl;
import com.fpt.myfschool.dto.request.SchoolClassRequest;
import com.fpt.myfschool.dto.response.SchoolClassResponse;
import com.fpt.myfschool.entity.SchoolClass;
import com.fpt.myfschool.exception.AppException;
import com.fpt.myfschool.exception.ErrorCode;
import com.fpt.myfschool.mapper.SchoolClassMapper;
import com.fpt.myfschool.repository.SchoolClassRepository;
import com.fpt.myfschool.service.SchoolClassService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchoolClassServiceImpl implements SchoolClassService {
    private final SchoolClassRepository classRepository;
    private final SchoolClassMapper classMapper;

    /**
     * Lấy danh sách Lớp học (Hỗ trợ phân trang và Tìm kiếm)
     * - Nếu có từ khóa 'search', query tìm kiếm tương đối (LIKE) trên cột Tên lớp.
     */
    @Override
    public Page<SchoolClassResponse> getClasses(String search, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<SchoolClass> classes = (search != null && !search.isEmpty()) ?
                classRepository.findByNameContainingIgnoreCase(search, pageable) :
                classRepository.findAll(pageable);
        return classes.map(classMapper::toDto);
    }

    /**
     * Tạo mới thông tin Lớp học
     * - MapRequest -> SchoolClass entity -> Lưu DB -> MapToDto
     */
    @Override
    public SchoolClassResponse createClass(SchoolClassRequest request) {
        SchoolClass entity = classMapper.toEntity(request);
        return classMapper.toDto(classRepository.save(entity));
    }

    /**
     * Cập nhật Lớp học
     * - Kiểm tra lớp học có tồn tại không. Nếu có thì ghi đè các tham số cơ bản.
     */
    @Override
    public SchoolClassResponse updateClass(Integer id, SchoolClassRequest request) {
        SchoolClass entity = classRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        entity.setName(request.getName());
        entity.setGrade(request.getGrade());
        entity.setAcademicYear(request.getAcademicYear());
        return classMapper.toDto(classRepository.save(entity));
    }

    @Override
    public void deleteClass(Integer id) {
        classRepository.deleteById(id);
    }
}