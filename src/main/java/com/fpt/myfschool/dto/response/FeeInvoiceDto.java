package com.fpt.myfschool.dto.response;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter @Setter
public class FeeInvoiceDto {
    private Long id;
    private Long studentId;
    private String studentName;
    private String studentRollNumber;
    private String title;
    private String semesterName;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private LocalDate dueDate;
    private String status;
}