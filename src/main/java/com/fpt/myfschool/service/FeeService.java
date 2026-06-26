package com.fpt.myfschool.service;
import com.fpt.myfschool.dto.request.GenerateFeeReqDto;
import com.fpt.myfschool.dto.response.FeeInvoiceDto;
import com.fpt.myfschool.dto.response.FeeTransactionDto;
import java.util.List;

public interface FeeService {
    List<FeeInvoiceDto> getStudentInvoices(Long studentId);
    List<FeeTransactionDto> getInvoiceTransactions(Long invoiceId);
    List<FeeInvoiceDto> getAllInvoices(Integer semesterId, Integer classId);
    void generateInvoicesForClass(GenerateFeeReqDto request);
}