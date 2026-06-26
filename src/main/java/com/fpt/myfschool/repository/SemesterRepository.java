package com.fpt.myfschool.repository;

import com.fpt.myfschool.entity.Semester;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Integer> {
    Page<Semester> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
