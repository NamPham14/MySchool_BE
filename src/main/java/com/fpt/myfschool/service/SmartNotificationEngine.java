package com.fpt.myfschool.service;

import com.fpt.myfschool.entity.Grade;
import com.fpt.myfschool.entity.LeaveRequest;
import com.fpt.myfschool.entity.Notification;
import com.fpt.myfschool.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmartNotificationEngine {
    private final NotificationRepository notificationRepo;

    public void notifyNewGrade(Grade grade) {
        if (grade.getStudent() == null) return;
        Notification notif = Notification.builder()
                .user(grade.getStudent())
                .title("Có điểm mới!")
                .message("Bạn vừa có điểm cập nhật cho môn " + (grade.getSubject() != null ? grade.getSubject().getName() : "học"))
                .type(Notification.NotificationType.GRADE)
                .relatedId(grade.getId())
                .build();
        notificationRepo.save(notif);
        log.info("Đã gửi thông báo điểm mới cho sinh viên: " + grade.getStudent().getId());
    }

    public void notifyLeaveReviewed(LeaveRequest leave) {
        if (leave.getStudent() == null) return;
        String statusStr = leave.getStatus() == LeaveRequest.LeaveStatus.APPROVED ? "ĐƯỢC DUYỆT" : "BỊ TỪ CHỐI";
        Notification notif = Notification.builder()
                .user(leave.getStudent())
                .title("Kết quả Đơn xin phép")
                .message("Đơn xin phép từ ngày " + leave.getStartDate() + " của bạn đã " + statusStr)
                .type(Notification.NotificationType.LEAVE)
                .relatedId(leave.getId())
                .build();
        notificationRepo.save(notif);
        log.info("Đã gửi thông báo duyệt đơn cho sinh viên: " + leave.getStudent().getId());
    }
}