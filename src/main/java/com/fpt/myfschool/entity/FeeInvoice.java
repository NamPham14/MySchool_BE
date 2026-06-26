package com.fpt.myfschool.entity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity @Table(name = "fee_invoices")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FeeInvoice {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @Column(length = 150)
    private String title;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "paid_amount", precision = 10, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private FeeStatus status;

    public enum FeeStatus { UNPAID, PARTIAL, PAID }
}