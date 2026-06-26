package com.fpt.myfschool.dto.response;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
public class FeeTransactionDto {
    private Long id;
    private BigDecimal amount;
    private String paymentMethod;
    private LocalDateTime transactionDate;
    private String status;
}