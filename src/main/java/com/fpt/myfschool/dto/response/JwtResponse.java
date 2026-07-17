package com.fpt.myfschool.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String refreshToken;
    private String type;
    private Long id;
    private String phoneNumber;
    private String fullName;
    private String avatarUrl;
    private String email;
    private String rollNumber;
    private String campus;
    private String className;
    private List<String> roles;

    public JwtResponse(String token, String refreshToken, Long id, String phoneNumber, String fullName, String avatarUrl, String email, String rollNumber, String campus, String className, List<String> roles) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.type = "Bearer";
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.email = email;
        this.rollNumber = rollNumber;
        this.campus = campus;
        this.className = className;
        this.roles = roles;
    }
}
