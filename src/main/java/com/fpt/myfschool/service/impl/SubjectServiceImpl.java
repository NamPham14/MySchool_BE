package com.fpt.myfschool.service.impl;
import com.fpt.myfschool.dto.request.SubjectRequest;
import com.fpt.myfschool.dto.response.SubjectResponse;
import com.fpt.myfschool.entity.Subject;
import com.fpt.myfschool.exception.AppException;
import com.fpt.myfschool.exception.ErrorCode;
import com.fpt.myfschool.mapper.SubjectMapper;
import com.fpt.myfschool.repository.SubjectRepository;
import com.fpt.myfschool.service.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl implements SubjectService {
    private final SubjectRepository subjectRepository;
    private final SubjectMapper subjectMapper;

    /**
     * Lấy danh sách Môn học (Hỗ trợ phân trang)
     * - Nếu tham số 'search' có giá trị, hệ thống sẽ gọi Repository để tìm các môn học có Tên hoặc Mã chứa từ khóa đó.
     * - Nếu không, hệ thống sẽ trả về toàn bộ dữ liệu có trong DB theo trang yêu cầu.
     * - Cuối cùng, dùng MapStruct để convert từ Entity sang DTO để giấu cấu trúc DB.
     */
    @Override
    public Page<SubjectResponse> getSubjects(String search, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Subject> subjects = (search != null && !search.isEmpty()) ?
                subjectRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(search, search, pageable) :
                subjectRepository.findAll(pageable);
        return subjects.map(subjectMapper::toDto);
    }

    /**
     * Tạo mới một Môn học
     * - Dùng SubjectMapper để convert DTO thành Entity.
     * - Gọi Repository để lưu vào Database.
     * - Convert Entity vừa lưu ngược lại thành DTO để trả về cho người dùng (kèm ID vừa được tạo).
     */
    @Override
    public SubjectResponse createSubject(SubjectRequest request) {
        Subject subject = subjectMapper.toEntity(request);
        return subjectMapper.toDto(subjectRepository.save(subject));
    }

    /**
     * Cập nhật thông tin Môn học
     * - Tìm kiếm Môn học trong DB theo ID. Nếu không có thì quăng lỗi (ném ra Global Exception Handler bắt).
     * - Ghi đè thông tin mới lên Entity (Tên, Mã).
     * - Lưu đè lại xuống DB và trả về DTO.
     */
    @Override
    public SubjectResponse updateSubject(Integer id, SubjectRequest request) {
        Subject subject = subjectRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        subject.setName(request.getName());
        subject.setCode(request.getCode());
        return subjectMapper.toDto(subjectRepository.save(subject));
    }

    /**
     * Xóa Môn học
     * - Gọi thẳng Repository để xóa môn học bằng ID.
     */
    @Override
    public void deleteSubject(Integer id) {
        subjectRepository.deleteById(id);
    }
}