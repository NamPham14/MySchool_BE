package com.fpt.myfschool.repository;

import com.fpt.myfschool.entity.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    
    /**
     * Tìm kiếm môn học theo Tên hoặc Mã môn học, không phân biệt hoa thường.
     * Dùng cho chức năng ô tìm kiếm (Search bar) có phân trang.
     */
    Page<Subject> findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(String name, String code, Pageable pageable);
}