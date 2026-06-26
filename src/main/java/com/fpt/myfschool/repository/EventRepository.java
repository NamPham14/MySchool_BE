package com.fpt.myfschool.repository;

import com.fpt.myfschool.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    /**
     * Lấy danh sách sự kiện theo trạng thái (UPCOMING, ONGOING, COMPLETED).
     * Phục vụ cho các Tab sự kiện trên giao diện Mobile.
     */
    List<Event> findByStatus(Event.EventStatus status);
}