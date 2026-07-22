package com.fpt.myfschool.service.impl;
import com.fpt.myfschool.dto.response.FeeInvoiceDto;
import com.fpt.myfschool.dto.response.FeeTransactionDto;
import com.fpt.myfschool.mapper.FeeMapper;
import com.fpt.myfschool.repository.FeeInvoiceRepository;
import com.fpt.myfschool.repository.FeeTransactionRepository;
import com.fpt.myfschool.service.FeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class FeeServiceImpl implements FeeService {
    private final FeeInvoiceRepository invoiceRepo;
    private final FeeTransactionRepository transRepo;
    private final com.fpt.myfschool.repository.UserRepository userRepo;
    private final com.fpt.myfschool.repository.SemesterRepository semesterRepo;
    private final FeeMapper feeMapper;

    /**
     * Lấy Hóa Đơn Học Phí Của Học Sinh
     * - Lấy ra tất cả các đợt thu tiền học mà Học sinh này phải đóng.
     * - Trả về số tiền tổng, số đã đóng và Tình trạng (UNPAID, PARTIAL, PAID).
     */
    @Override
    public List<FeeInvoiceDto> getStudentInvoices(Long studentId) {
        return invoiceRepo.findByStudentIdOrderByDueDateDesc(studentId).stream()
                .map(feeMapper::toInvoiceDto).collect(Collectors.toList());
    }

    /**
     * Lấy danh sách tất cả Hóa đơn Học phí (Dành cho Giáo viên)
     */
    @Override
    public List<FeeInvoiceDto> getAllInvoices(Integer semesterId, Integer classId) {
        List<com.fpt.myfschool.entity.FeeInvoice> invoices;
        if (classId != null && semesterId != null) {
            invoices = invoiceRepo.findBySemesterAndClassOrderByDueDateDesc(semesterId, classId);
        } else if (semesterId != null) {
            invoices = invoiceRepo.findBySemesterIdOrderByDueDateDesc(semesterId);
        } else {
            invoices = invoiceRepo.findAll();
        }
        return invoices.stream().map(feeMapper::toInvoiceDto).collect(Collectors.toList());
    }
    @Override
    public void generateInvoicesForClass(com.fpt.myfschool.dto.request.GenerateFeeReqDto request) {
        // Tìm học sinh theo classId
        List<com.fpt.myfschool.entity.User> students = userRepo.findBySchoolClassIdAndRolesName(request.getClassId(), "STUDENT");
        
        com.fpt.myfschool.entity.Semester semester = semesterRepo.findById(request.getSemesterId())
            .orElseThrow(() -> new com.fpt.myfschool.exception.AppException(com.fpt.myfschool.exception.ErrorCode.RESOURCE_NOT_FOUND));

        List<com.fpt.myfschool.entity.FeeInvoice> invoicesToSave = new java.util.ArrayList<>();
        
        for (com.fpt.myfschool.entity.User student : students) {
            // Kiểm tra xem đã có hóa đơn cho kỳ này chưa
            boolean exists = invoiceRepo.findByStudentIdOrderByDueDateDesc(student.getId()).stream()
                .anyMatch(inv -> inv.getSemester().getId().equals(request.getSemesterId()));
                
            if (!exists) {
                com.fpt.myfschool.entity.FeeInvoice invoice = com.fpt.myfschool.entity.FeeInvoice.builder()
                    .student(student)
                    .semester(semester)
                    .title(request.getTitle())
                    .amount(request.getAmount())
                    .paidAmount(BigDecimal.ZERO)
                    .dueDate(request.getDueDate())
                    .status(com.fpt.myfschool.entity.FeeInvoice.FeeStatus.UNPAID)
                    .build();
                invoicesToSave.add(invoice);
            }
        }
        
        if (!invoicesToSave.isEmpty()) {
            invoiceRepo.saveAll(invoicesToSave);
        }
    }
    /**
     * Lấy Lịch Sử Thanh Toán
     * - Khi học sinh ấn vào 1 Hóa đơn, hàm này trả về các giao dịch VNPAY/Tiền mặt họ đã từng chuyển khoản để thanh toán cho hóa đơn đó.
     */
    @Override
    public List<FeeTransactionDto> getInvoiceTransactions(Long invoiceId) {
        return transRepo.findByFeeInvoiceIdOrderByTransactionDateDesc(invoiceId).stream()
                .map(feeMapper::toTransactionDto).collect(Collectors.toList());
    }

    @Override
    public FeeInvoiceDto updateInvoice(Long invoiceId, com.fpt.myfschool.dto.request.UpdateFeeReqDto request) {
        com.fpt.myfschool.entity.FeeInvoice invoice = invoiceRepo.findById(invoiceId)
            .orElseThrow(() -> new com.fpt.myfschool.exception.AppException(com.fpt.myfschool.exception.ErrorCode.RESOURCE_NOT_FOUND));

        if (request.getTitle() != null && !request.getTitle().trim().isEmpty()) {
            invoice.setTitle(request.getTitle());
        }
        if (request.getAmount() != null) {
            invoice.setAmount(request.getAmount());
        }
        if (request.getDueDate() != null) {
            invoice.setDueDate(request.getDueDate());
        }

        invoiceRepo.save(invoice);
        return feeMapper.toInvoiceDto(invoice);
    }

    @Override
    public void deleteInvoice(Long invoiceId) {
        com.fpt.myfschool.entity.FeeInvoice invoice = invoiceRepo.findById(invoiceId)
            .orElseThrow(() -> new com.fpt.myfschool.exception.AppException(com.fpt.myfschool.exception.ErrorCode.RESOURCE_NOT_FOUND));
            
        // Kiểm tra nếu đã thanh toán một phần thì không cho xóa
        if (invoice.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new RuntimeException("Không thể xóa hóa đơn đã có giao dịch thanh toán");
        }
        
        invoiceRepo.delete(invoice);
    }
}