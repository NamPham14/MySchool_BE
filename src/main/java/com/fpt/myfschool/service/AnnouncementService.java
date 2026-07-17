package com.fpt.myfschool.service;

import com.fpt.myfschool.dto.request.AnnouncementRequest;
import com.fpt.myfschool.dto.response.AnnouncementResponse;

import java.util.List;

public interface AnnouncementService {
    AnnouncementResponse createAnnouncement(AnnouncementRequest request);
    List<AnnouncementResponse> getAnnouncementsByClass(Integer classId);
    List<AnnouncementResponse> getMyAnnouncements();
    void deleteAnnouncement(Long id);
}
