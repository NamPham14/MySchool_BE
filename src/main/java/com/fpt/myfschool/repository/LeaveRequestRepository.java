package com.fpt.myfschool.repository;

import com.fpt.myfschool.entity.LeaveRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    
    /**
     * Lấy toàn bộ lịch sử xin nghỉ phép của một Học sinh.
     * Tự động sắp xếp đơn xin phép mới nhất lên đầu tiên.
     */
    List<LeaveRequest> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    @Query("SELECT lr FROM LeaveRequest lr WHERE lr.student.schoolClass.id = :classId ORDER BY lr.createdAt DESC")
    List<LeaveRequest> findByClassIdOrderByCreatedAtDesc(@org.springframework.data.repository.query.Param("classId") Integer classId);

    List<LeaveRequest> findAllByOrderByCreatedAtDesc();
    
    long countByStatus(LeaveRequest.LeaveStatus status);
}