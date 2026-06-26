package com.fpt.myfschool.repository;

import com.fpt.myfschool.entity.FeeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeeTransactionRepository extends JpaRepository<FeeTransaction, Long> {
    
    /**
     * Lấy danh sách lịch sử đóng tiền của một Hóa đơn học phí.
     * Sắp xếp theo thời gian giao dịch mới nhất lên đầu.
     */
    List<FeeTransaction> findByFeeInvoiceIdOrderByTransactionDateDesc(Long invoiceId);
}