package com.fpt.myfschool.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "grades")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "semester_id", nullable = false)
    private Semester semester;

    @Column(name = "regular_score_1", columnDefinition = "DECIMAL(4,2)")
    private Double regularScore1;

    @Column(name = "regular_score_2", columnDefinition = "DECIMAL(4,2)")
    private Double regularScore2;

    @Column(name = "regular_score_3", columnDefinition = "DECIMAL(4,2)")
    private Double regularScore3;

    @Column(name = "regular_score_4", columnDefinition = "DECIMAL(4,2)")
    private Double regularScore4;

    @Column(name = "midterm_score", columnDefinition = "DECIMAL(4,2)")
    private Double midtermScore;

    @Column(name = "final_score", columnDefinition = "DECIMAL(4,2)")
    private Double finalScore;

    @Column(name = "average_score", columnDefinition = "DECIMAL(4,2)")
    private Double averageScore;
}
