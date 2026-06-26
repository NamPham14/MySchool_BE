package com.fpt.myfschool.repository;

import com.fpt.myfschool.entity.SchoolClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Integer> {
    
    /**
     * Tìm kiếm lớp học theo tên lớp (Ví dụ: 10A1, 11B2...).
     * Hỗ trợ tìm kiếm tương đối và phân trang.
     */
    Page<SchoolClass> findByNameContainingIgnoreCase(String name, Pageable pageable);
}