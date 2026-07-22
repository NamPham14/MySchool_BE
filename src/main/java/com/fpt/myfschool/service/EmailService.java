package com.fpt.myfschool.service;

import com.fpt.myfschool.entity.Club;
import com.fpt.myfschool.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Async
    public void sendClubApprovalEmail(String studentEmail, String studentName, String clubName, boolean isApproved) {
        if (studentEmail == null || studentEmail.isEmpty()) {
            return; // Khong the gui email
        }

        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(senderEmail);
            mailMessage.setTo(studentEmail);

            if (isApproved) {
                mailMessage.setSubject("Kết quả duyệt thành viên Câu lạc bộ - Đã duyệt");
                mailMessage.setText(String.format(
                        "Chào %s,\n\n" +
                                "Chúc mừng em! Đơn đăng ký tham gia câu lạc bộ \"%s\" của em đã được nhà trường DUYỆT.\n" +
                                "Bây giờ em đã trở thành thành viên chính thức của câu lạc bộ. Hãy đăng nhập vào hệ thống để xem chi tiết nhé!\n\n" +
                                "Trân trọng,\nBan quản lý MyFSchool",
                        studentName, clubName
                ));
            } else {
                mailMessage.setSubject("Kết quả duyệt thành viên Câu lạc bộ - Từ chối");
                mailMessage.setText(String.format(
                        "Chào %s,\n\n" +
                                "Rất tiếc phải thông báo rằng đơn đăng ký tham gia câu lạc bộ \"%s\" của em đã BỊ TỪ CHỐI (có thể do số lượng thành viên đã đạt tối đa hoặc chưa phù hợp tiêu chí).\n" +
                                "Em có thể tìm hiểu và đăng ký các câu lạc bộ khác trên hệ thống.\n\n" +
                                "Trân trọng,\nBan quản lý MyFSchool",
                        studentName, clubName
                ));
            }

            javaMailSender.send(mailMessage);
            System.out.println("Đã gửi email thông báo CLB thành công đến: " + studentEmail);
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi email thông báo duyệt CLB: " + e.getMessage());
        }
    }
}
