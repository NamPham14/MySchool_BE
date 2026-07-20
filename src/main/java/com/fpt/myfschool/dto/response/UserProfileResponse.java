package com.fpt.myfschool.dto.response;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserProfileResponse {
    private Long id;
    private String email;
    private String fullName;
    private String rollNumber;
    private String avatarUrl;
    private String phoneNumber;
    private String campus;
    private String status;
    private LocalDateTime createdAt;
    private String className;
    private java.util.List<String> roles;
}