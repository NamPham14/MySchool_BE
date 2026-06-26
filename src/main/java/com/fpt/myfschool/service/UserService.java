package com.fpt.myfschool.service;
import com.fpt.myfschool.dto.request.UpdateProfileRequest;
import com.fpt.myfschool.dto.response.UserProfileResponse;

import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserProfileResponse getMyProfile(Long userId);
    UserProfileResponse updateMyProfile(Long userId, UpdateProfileRequest request);
    String uploadAvatar(Long userId, MultipartFile file) throws java.io.IOException;
    
    java.util.List<UserProfileResponse> getStudentsByClass(Integer classId);
    java.util.List<UserProfileResponse> getAllStudents();
    java.util.List<UserProfileResponse> getAllTeachers();
    void assignStudentToClass(Long studentId, Integer classId);
    UserProfileResponse createStudent(com.fpt.myfschool.dto.request.RegisterRequest request, Integer classId);
}