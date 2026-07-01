package com.fpt.myfschool.service;

import com.fpt.myfschool.entity.Grade;
import com.fpt.myfschool.entity.LeaveRequest;
import com.fpt.myfschool.entity.Notification;
import com.fpt.myfschool.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import org.springframework.messaging.simp.SimpMessagingTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class SmartNotificationEngine {
    private final NotificationRepository notificationRepo;
    private final SimpMessagingTemplate messagingTemplate;

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
        
        // Push realtime via WebSocket
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + grade.getStudent().getId(), 
                "NEW_NOTIFICATION"
        );
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
        
        // Push realtime via WebSocket
        messagingTemplate.convertAndSend(
                "/topic/notifications/" + leave.getStudent().getId(), 
                "NEW_NOTIFICATION"
        );
    }

    public void notifyNewAssignment(com.fpt.myfschool.entity.Assignment assignment, java.util.List<com.fpt.myfschool.entity.User> students) {
        if (students == null || students.isEmpty()) return;
        java.util.List<Notification> notifs = new java.util.ArrayList<>();
        for (com.fpt.myfschool.entity.User student : students) {
            Notification notif = Notification.builder()
                    .user(student)
                    .title("Bài tập mới!")
                    .message("Giáo viên " + assignment.getTeacher().getFullName() + " vừa giao bài tập mới môn " + assignment.getSubject().getName())
                    .type(Notification.NotificationType.ASSIGNMENT)
                    .relatedId(assignment.getId())
                    .build();
            notifs.add(notif);
        }
        notificationRepo.saveAll(notifs);
        
        // Push realtime
        for (com.fpt.myfschool.entity.User student : students) {
            messagingTemplate.convertAndSend(
                    "/topic/notifications/" + student.getId(), 
                    "NEW_NOTIFICATION"
            );
        }
        log.info("Đã gửi thông báo bài tập mới cho " + students.size() + " sinh viên.");
    }
}