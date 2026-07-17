package com.fpt.myfschool.service.impl;

import com.fpt.myfschool.dto.request.AnnouncementRequest;
import com.fpt.myfschool.dto.response.AnnouncementResponse;
import com.fpt.myfschool.entity.Announcement;
import com.fpt.myfschool.entity.SchoolClass;
import com.fpt.myfschool.entity.User;
import com.fpt.myfschool.exception.AppException;
import com.fpt.myfschool.exception.ErrorCode;
import com.fpt.myfschool.mapper.AnnouncementMapper;
import com.fpt.myfschool.repository.AnnouncementRepository;
import com.fpt.myfschool.repository.SchoolClassRepository;
import com.fpt.myfschool.repository.UserRepository;
import com.fpt.myfschool.entity.Notification;
import com.fpt.myfschool.repository.NotificationRepository;
import com.fpt.myfschool.mapper.NotificationMapper;
import com.fpt.myfschool.dto.response.NotificationResponse;
import com.fpt.myfschool.security.UserDetailsImpl;
import com.fpt.myfschool.service.AnnouncementService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final SchoolClassRepository schoolClassRepository;
    private final UserRepository userRepository;
    private final AnnouncementMapper announcementMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    private Long getCurrentUserId() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getId();
    }

    @Override
    @Transactional
    public AnnouncementResponse createAnnouncement(AnnouncementRequest request) {
        Long teacherId = getCurrentUserId();
        User teacher = userRepository.findById(teacherId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        SchoolClass schoolClass = schoolClassRepository.findById(request.getClassId())
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .schoolClass(schoolClass)
                .teacher(teacher)
                .build();

        Announcement saved = announcementRepository.save(announcement);
        AnnouncementResponse response = announcementMapper.toResponse(saved);

        // Push realtime via WebSocket to the class channel
        messagingTemplate.convertAndSend("/topic/class/" + request.getClassId() + "/announcements", response);

        // Tạo thông báo (Notification) cho tất cả học sinh trong lớp
        List<User> students = userRepository.findBySchoolClassIdAndRolesName(request.getClassId(), "STUDENT");
        List<Notification> notifications = students.stream().map(student -> Notification.builder()
                .user(student)
                .title("Thông báo mới: " + announcement.getTitle())
                .message(announcement.getContent())
                .type(Notification.NotificationType.ANNOUNCEMENT)
                .relatedId(saved.getId())
                .isRead(false)
                .build()).collect(Collectors.toList());

        List<Notification> savedNotifs = notificationRepository.saveAll(notifications);

        // Bắn WebSocket thông báo cá nhân cho từng học sinh để cập nhật chuông (Badge)
        for (Notification notif : savedNotifs) {
            NotificationResponse notifRes = notificationMapper.toDto(notif);
            messagingTemplate.convertAndSend("/topic/notifications/" + notif.getUser().getId(), notifRes);
        }

        return response;
    }

    @Override
    public List<AnnouncementResponse> getAnnouncementsByClass(Integer classId) {
        List<Announcement> announcements = announcementRepository.findBySchoolClassIdOrderByCreatedAtDesc(classId);
        return announcements.stream()
                .map(announcementMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<AnnouncementResponse> getMyAnnouncements() {
        Long userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        if (user.getSchoolClass() == null) {
            return List.of();
        }

        List<Announcement> announcements = announcementRepository.findBySchoolClassIdOrderByCreatedAtDesc(user.getSchoolClass().getId());
        return announcements.stream()
                .map(announcementMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));

        Long currentUserId = getCurrentUserId();
        if (!announcement.getTeacher().getId().equals(currentUserId)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        announcementRepository.delete(announcement);
    }
}
