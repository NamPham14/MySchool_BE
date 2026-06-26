package com.fpt.myfschool.service;

import com.fpt.myfschool.dto.request.TimetableRequest;
import com.fpt.myfschool.dto.response.TimetableResponse;
import java.util.List;

public interface ScheduleService {
    List<TimetableResponse> getSchedulesByClass(Integer classId);
    TimetableResponse getNextClass(Integer classId);
    TimetableResponse createSchedule(TimetableRequest request);
    TimetableResponse updateSchedule(Long id, TimetableRequest request);
    void deleteSchedule(Long id);
}
