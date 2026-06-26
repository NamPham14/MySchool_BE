package com.fpt.myfschool.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "academic_summaries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @Column(columnDefinition = "DECIMAL(4,2)")
    private Double gpa;

    @Column(name = "academic_performance", length = 50)
    private String academicPerformance;

    @Column(length = 50)
    private String conduct;
}
