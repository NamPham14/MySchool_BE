package com.fpt.myfschool.repository;
import com.fpt.myfschool.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
    
    /**
     * Lấy toàn bộ điểm các môn học của một học sinh trong một học kỳ.
     * Dùng để render danh sách bảng điểm chi tiết.
     */
    List<Grade> findByStudentIdAndSemesterId(Long studentId, Integer semesterId);
    
    /**
     * Tìm điểm của một học sinh cho đúng môn học đó ở học kỳ đó.
     * Hàm này quan trọng vì 1 học sinh chỉ có duy nhất 1 con điểm trung bình môn cho 1 môn trong 1 kỳ.
     */
    Optional<Grade> findByStudentIdAndSubjectIdAndSemesterId(Long studentId, Integer subjectId, Integer semesterId);
}