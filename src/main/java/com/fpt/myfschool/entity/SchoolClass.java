package com.fpt.myfschool.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchoolClass {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    private Integer grade;

    @Column(name = "academic_year", length = 20)
    private String academicYear;
}
