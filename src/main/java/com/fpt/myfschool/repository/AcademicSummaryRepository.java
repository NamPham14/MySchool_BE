package com.fpt.myfschool.repository;
import com.fpt.myfschool.entity.AcademicSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AcademicSummaryRepository extends JpaRepository<AcademicSummary, Long> {
    
    /**
     * Tìm Tổng Kết Học Tập của một Học Sinh trong một Học Kỳ cụ thể.
     * Hàm này được dùng để lấy ra điểm GPA trung bình và xếp loại của học sinh đó.
     * Nếu học sinh chưa có điểm tổng kết cho học kỳ đó, nó sẽ trả về Optional.empty().
     */
    Optional<AcademicSummary> findByStudentIdAndSemesterId(Long studentId, Integer semesterId);
}