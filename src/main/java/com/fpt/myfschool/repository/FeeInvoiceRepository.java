package com.fpt.myfschool.repository;

import com.fpt.myfschool.entity.FeeInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeInvoiceRepository extends JpaRepository<FeeInvoice, Long> {
    
    /**
     * Lấy danh sách Hóa đơn học phí của Học sinh.
     * Sắp xếp theo hạn đóng tiền giảm dần (hóa đơn mới nhất lên đầu).
     */
    List<FeeInvoice> findByStudentIdOrderByDueDateDesc(Long studentId);

    @Query("SELECT f FROM FeeInvoice f WHERE f.semester.id = :semesterId ORDER BY f.dueDate DESC")
    List<FeeInvoice> findBySemesterIdOrderByDueDateDesc(@org.springframework.data.repository.query.Param("semesterId") Integer semesterId);

    @Query("SELECT f FROM FeeInvoice f WHERE f.semester.id = :semesterId AND f.student.schoolClass.id = :classId ORDER BY f.dueDate DESC")
    List<FeeInvoice> findBySemesterAndClassOrderByDueDateDesc(@org.springframework.data.repository.query.Param("semesterId") Integer semesterId, @org.springframework.data.repository.query.Param("classId") Integer classId);

    @org.springframework.data.jpa.repository.Query("SELECT SUM(f.paidAmount) FROM FeeInvoice f WHERE f.status = :status")
    java.math.BigDecimal sumTotalRevenue(@org.springframework.data.repository.query.Param("status") FeeInvoice.FeeStatus status);

    @org.springframework.data.jpa.repository.Query(value = "SELECT MONTH(due_date) as monthVal, SUM(paid_amount) as total FROM fee_invoices WHERE status = 'PAID' GROUP BY MONTH(due_date) ORDER BY monthVal", nativeQuery = true)
    List<Object[]> sumRevenueByMonth();
}