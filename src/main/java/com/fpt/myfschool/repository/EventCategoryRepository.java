package com.fpt.myfschool.repository;

import com.fpt.myfschool.entity.EventCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventCategoryRepository extends JpaRepository<EventCategory, Long> {
}
