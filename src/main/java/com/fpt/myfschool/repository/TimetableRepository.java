package com.fpt.myfschool.repository;

import com.fpt.myfschool.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    
    /**
     * Lấy toàn bộ lịch học của một Lớp học cụ thể.
     */
    List<Timetable> findBySchoolClassId(Integer classId);
    
    /**
     * Lấy lịch học của Lớp và sắp xếp theo thứ tự: Ngày trong tuần (Thứ 2 -> CN) -> Giờ bắt đầu.
     * Dùng để render thời khóa biểu trên App Mobile.
     */
    List<Timetable> findBySchoolClassIdOrderByDayOfWeekAscStartTimeAsc(Integer classId);
}