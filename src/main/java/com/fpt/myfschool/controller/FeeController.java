package com.fpt.myfschool.controller;
import com.fpt.myfschool.dto.response.APIResponse;
import com.fpt.myfschool.dto.response.FeeInvoiceDto;
import com.fpt.myfschool.dto.response.FeeTransactionDto;
import com.fpt.myfschool.security.UserDetailsImpl;
import com.fpt.myfschool.service.FeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;

@RestController
@RequestMapping("/api/fees")
@RequiredArgsConstructor
public class FeeController {
    private final FeeService feeService;

    private Long getCurrentUserId() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }

    /**
     * [DÀNH CHO HỌC SINH]
     * API xem danh sách học phí cần đóng hoặc đã đóng (của chính tôi)
     */
    @GetMapping("/my-invoices")
    public ResponseEntity<APIResponse<List<FeeInvoiceDto>>> getMyInvoices(
            @RequestParam(required = false) Long studentId) {
        Long sId = (studentId != null) ? studentId : getCurrentUserId();
        List<FeeInvoiceDto> data = feeService.getStudentInvoices(sId);
        return ResponseEntity.ok(APIResponse.<List<FeeInvoiceDto>>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }

    /**
     * [DÀNH CHO HỌC SINH]
     * API xem danh sách học phí cần đóng hoặc đã đóng
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<APIResponse<List<FeeInvoiceDto>>> getInvoices(@PathVariable Long studentId) {
        List<FeeInvoiceDto> data = feeService.getStudentInvoices(studentId);
        return ResponseEntity.ok(APIResponse.<List<FeeInvoiceDto>>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN/ADMIN]
     * API xem danh sách học phí của toàn trường hoặc lọc theo Lớp, Kỳ
     */
    @GetMapping
    public ResponseEntity<APIResponse<List<FeeInvoiceDto>>> getAllInvoices(
            @RequestParam(required = false) Integer semesterId,
            @RequestParam(required = false) Integer classId) {
        List<FeeInvoiceDto> data = feeService.getAllInvoices(semesterId, classId);
        return ResponseEntity.ok(APIResponse.<List<FeeInvoiceDto>>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }

    /**
     * [DÀNH CHO GIÁO VIÊN/ADMIN]
     * API tạo hóa đơn học phí cho cả lớp
     */
    @PostMapping("/generate")
    public ResponseEntity<APIResponse<String>> generateInvoices(@RequestBody com.fpt.myfschool.dto.request.GenerateFeeReqDto req) {
        feeService.generateInvoicesForClass(req);
        return ResponseEntity.ok(APIResponse.<String>builder()
                .status(200).code(1000).message("Phát sinh hóa đơn thành công").data("OK").build());
    }

    /**
     * [DÀNH CHO HỌC SINH]
     * API xem lịch sử giao dịch (Thanh toán qua VNPAY, Tiền mặt)
     */
    @GetMapping("/invoice/{invoiceId}/transactions")
    public ResponseEntity<APIResponse<List<FeeTransactionDto>>> getTransactions(@PathVariable Long invoiceId) {
        List<FeeTransactionDto> data = feeService.getInvoiceTransactions(invoiceId);
        return ResponseEntity.ok(APIResponse.<List<FeeTransactionDto>>builder()
                .status(200).code(1000).message("Thành công").data(data).build());
    }
}