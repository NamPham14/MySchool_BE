package com.fpt.myfschool.service.impl;
import com.fpt.myfschool.dto.response.EventResDto;
import com.fpt.myfschool.entity.Event;
import com.fpt.myfschool.exception.AppException;
import com.fpt.myfschool.exception.ErrorCode;
import com.fpt.myfschool.mapper.EventMapper;
import com.fpt.myfschool.repository.EventCategoryRepository;
import com.fpt.myfschool.repository.EventRepository;
import com.fpt.myfschool.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepo;
    private final EventCategoryRepository categoryRepo;
    private final EventMapper eventMapper;

    @Override
    public EventResDto getEventDetail(Long id) {
        Event e = eventRepo.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        return eventMapper.toDto(e);
    }

    @Override
    public List<EventResDto> getEventsByStatus(String status) {
        Event.EventStatus evStatus = Event.EventStatus.valueOf(status.toUpperCase());
        return eventRepo.findByStatus(evStatus).stream().map(eventMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<EventResDto> getAllEvents() {
        return eventRepo.findAll().stream().map(eventMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public EventResDto createEvent(com.fpt.myfschool.dto.request.EventReqDto req) {
        Event e = new Event();
        e.setTitle(req.getTitle());
        e.setStartDatetime(req.getStartDatetime());
        e.setEndDatetime(req.getEndDatetime());
        e.setLocation(req.getLocation());
        e.setImageUrl(req.getImageUrl());
        e.setDescription(req.getDescription());
        if (req.getCategoryId() != null) {
            e.setCategory(categoryRepo.findById(req.getCategoryId()).orElse(null));
        }
        if (req.getStatus() != null) {
            e.setStatus(Event.EventStatus.valueOf(req.getStatus().toUpperCase()));
        } else {
            e.setStatus(Event.EventStatus.UPCOMING);
        }
        Event saved = eventRepo.save(e);
        return eventMapper.toDto(saved);
    }

    @Override
    public EventResDto updateEvent(Long id, com.fpt.myfschool.dto.request.EventReqDto req) {
        Event e = eventRepo.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        e.setTitle(req.getTitle());
        e.setStartDatetime(req.getStartDatetime());
        e.setEndDatetime(req.getEndDatetime());
        e.setLocation(req.getLocation());
        e.setImageUrl(req.getImageUrl());
        e.setDescription(req.getDescription());
        if (req.getCategoryId() != null) {
            e.setCategory(categoryRepo.findById(req.getCategoryId()).orElse(null));
        }
        if (req.getStatus() != null) {
            e.setStatus(Event.EventStatus.valueOf(req.getStatus().toUpperCase()));
        }
        Event saved = eventRepo.save(e);
        return eventMapper.toDto(saved);
    }

    @Override
    public void deleteEvent(Long id) {
        Event e = eventRepo.findById(id).orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        eventRepo.delete(e);
    }

    @Override
    public List<com.fpt.myfschool.entity.EventCategory> getAllEventCategories() {
        return categoryRepo.findAll();
    }
}