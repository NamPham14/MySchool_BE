package com.fpt.myfschool.service.impl;
import com.fpt.myfschool.dto.response.NotificationResponse;
import com.fpt.myfschool.entity.Notification;
import com.fpt.myfschool.exception.AppException;
import com.fpt.myfschool.exception.ErrorCode;
import com.fpt.myfschool.mapper.NotificationMapper;
import com.fpt.myfschool.repository.NotificationRepository;
import com.fpt.myfschool.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepo;
    private final NotificationMapper notificationMapper;

    @Override
    public List<NotificationResponse> getMyNotifications(Long userId) {
        return notificationRepo.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(notificationMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long notificationId) {
        Notification notif = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.RESOURCE_NOT_FOUND));
        notif.setIsRead(true);
        notificationRepo.save(notif);
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationRepo.countByUserIdAndIsReadFalse(userId);
    }
}