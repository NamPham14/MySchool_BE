package com.fpt.myfschool.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "fee_transactions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FeeTransaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fee_invoice_id", nullable = false)
    private FeeInvoice feeInvoice;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TransStatus status;

    public enum TransStatus { SUCCESS, FAILED, PENDING }
}