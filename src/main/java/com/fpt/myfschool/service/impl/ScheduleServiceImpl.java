package com.fpt.myfschool.service.impl;
import com.fpt.myfschool.dto.request.TimetableRequest;
import com.fpt.myfschool.dto.response.TimetableResponse;
import com.fpt.myfschool.entity.SchoolClass;
import com.fpt.myfschool.entity.Subject;
import com.fpt.myfschool.entity.Timetable;
import com.fpt.myfschool.entity.User;
import com.fpt.myfschool.exception.AppException;
import com.fpt.myfschool.exception.ErrorCode;
import com.fpt.myfschool.mapper.TimetableMapper;
import com.fpt.myfschool.repository.SchoolClassRepository;
import com.fpt.myfschool.repository.SubjectRepository;
import com.fpt.myfschool.repository.TimetableRepository;
import com.fpt.myfschool.repository.UserRepository;
import com.fpt.myfschool.service.ScheduleService;
import com.fpt.myfschool.util.TimetableExcelHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {
    private final TimetableRepository timetableRepository;
    private final SchoolClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final UserRepository userRepository;
    private final TimetableMapper timetableMapper;

    /**
     * Lấy Thời Khóa Biểu Của Lớp Học
     * - Gọi Repository để lấy tất cả ca học của lớp classId.
     * - Dữ liệu trả về sẽ được tự động xếp theo Thứ trong tuần (T2->CN) và sau đó xếp theo Giờ bắt đầu.
     * - Dùng MapStruct lấy luôn tên Môn Học và tên Giáo Viên để trả về Mobile hiển thị.
     */
    @Override
    public List<TimetableResponse> getSchedulesByClass(Integer classId) {
        return timetableRepository.findBySchoolClassIdOrderByDayOfWeekAscStartTimeAsc(classId)
                .stream().map(timetableMapper::toDto).collect(Collectors.toList());
    }

    /**
     * Tìm Tiết Học Tiếp Theo
     * - Lấy TKB của lớp, do Repository đã sort sẵn nên chỉ cần bốc phần tử đầu tiên (index 0).
     * - Lưu ý: Logic này cơ bản, nếu muốn chuẩn hơn ở Phase sau sẽ dùng LocalTime.now() so sánh.
     */
    @Override
    public TimetableResponse getNextClass(Integer classId) {
        List<Timetable> timetables = timetableRepository.findBySchoolClassIdOrderByDayOfWeekAscStartTimeAsc(classId);
        if(timetables.isEmpty()) return null;
        return timetableMapper.toDto(timetables.get(0));
    }

    /**
     * Xếp Lịch Học Mới
     * - Yêu cầu phải truyền vào 3 ID: Lớp, Môn và Giáo viên.
     * - Nếu 1 trong 3 ID không tồn tại => Văng Exception.
     * - Nối khóa ngoại (Relationship) từ các Entity cha vào Timetable trước khi lưu.
     */
    @Override
    public TimetableResponse createSchedule(TimetableRequest request) {
        Timetable timetable = timetableMapper.toEntity(request);
        SchoolClass schoolClass = classRepository.findById(request.getClassId()).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        Subject subject = subjectRepository.findById(request.getSubjectId()).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        User teacher = userRepository.findById(request.getTeacherId()).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        
        timetable.setSchoolClass(schoolClass);
        timetable.setSubject(subject);
        timetable.setTeacher(teacher);
        
        return timetableMapper.toDto(timetableRepository.save(timetable));
    }

    @Override
    public TimetableResponse updateSchedule(Long id, TimetableRequest request) {
        Timetable timetable = timetableRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        
        if (request.getClassId() != null) {
            SchoolClass schoolClass = classRepository.findById(request.getClassId()).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
            timetable.setSchoolClass(schoolClass);
        }
        if (request.getSubjectId() != null) {
            Subject subject = subjectRepository.findById(request.getSubjectId()).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
            timetable.setSubject(subject);
        }
        if (request.getTeacherId() != null) {
            User teacher = userRepository.findById(request.getTeacherId()).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
            timetable.setTeacher(teacher);
        }
        timetable.setDayOfWeek(request.getDayOfWeek());
        timetable.setPeriod(request.getPeriod());
        timetable.setStartTime(request.getStartTime());
        timetable.setEndTime(request.getEndTime());
        timetable.setRoom(request.getRoom());
        timetable.setNote(request.getNote());
        timetable.setIsExam(request.getIsExam());
        
        return timetableMapper.toDto(timetableRepository.save(timetable));
    }

    @Override
    public void deleteSchedule(Long id) {
        timetableRepository.deleteById(id);
    }

    @Override
    public java.io.ByteArrayInputStream exportTemplate() {
        List<SchoolClass> classes = classRepository.findAll();
        List<Subject> subjects = subjectRepository.findAll();
        List<User> teachers = userRepository.findByRolesName("TEACHER");
        return TimetableExcelHelper.createTemplate(classes, subjects, teachers);
    }

    @Override
    @Transactional
    public List<TimetableResponse> importExcel(MultipartFile file, boolean overwrite) {
        try {
            List<SchoolClass> classes = classRepository.findAll();
            List<Subject> subjects = subjectRepository.findAll();
            List<User> teachers = userRepository.findByRolesName("TEACHER");
            
            List<TimetableRequest> requests = TimetableExcelHelper.parseExcel(file.getInputStream(), classes, subjects, teachers);
            
            if (overwrite) {
                // Delete existing timetables for classes that are present in the import file
                List<Integer> classIds = requests.stream().map(TimetableRequest::getClassId).distinct().collect(Collectors.toList());
                for (Integer classId : classIds) {
                    List<Timetable> existing = timetableRepository.findBySchoolClassIdOrderByDayOfWeekAscStartTimeAsc(classId);
                    timetableRepository.deleteAll(existing);
                }
            }
            
            List<TimetableResponse> responses = new ArrayList<>();
            for (TimetableRequest req : requests) {
                responses.add(createSchedule(req));
            }
            return responses;
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to read Excel file", e);
        }
    }
}