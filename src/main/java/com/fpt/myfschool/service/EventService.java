package com.fpt.myfschool.service;
import com.fpt.myfschool.dto.response.EventResDto;
import java.util.List;

public interface EventService {
    List<EventResDto> getEventsByStatus(String status);
    EventResDto getEventDetail(Long id);
    List<EventResDto> getAllEvents();
    EventResDto createEvent(com.fpt.myfschool.dto.request.EventReqDto req);
    EventResDto updateEvent(Long id, com.fpt.myfschool.dto.request.EventReqDto req);
    void deleteEvent(Long id);
    List<com.fpt.myfschool.entity.EventCategory> getAllEventCategories();
}