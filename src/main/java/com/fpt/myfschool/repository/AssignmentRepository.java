package com.fpt.myfschool.repository;

import com.fpt.myfschool.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    
    /**
     * Lấy danh sách bài tập/nhắc việc của một lớp học cụ thể.
     * Sắp xếp theo ngày đến hạn (Due Date) tăng dần để học sinh biết bài nào sắp hết hạn trước.
     */
    List<Assignment> findBySchoolClassIdOrderByDueDateAsc(Integer classId);
}