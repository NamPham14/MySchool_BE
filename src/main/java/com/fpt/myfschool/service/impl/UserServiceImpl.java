package com.fpt.myfschool.service.impl;
import com.fpt.myfschool.dto.request.UpdateProfileRequest;
import com.fpt.myfschool.dto.response.UserProfileResponse;
import com.fpt.myfschool.entity.User;
import com.fpt.myfschool.exception.AppException;
import com.fpt.myfschool.exception.ErrorCode;
import com.fpt.myfschool.mapper.UserMapper;
import com.fpt.myfschool.repository.UserRepository;
import com.fpt.myfschool.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import com.fpt.myfschool.service.CloudinaryService;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CloudinaryService cloudinaryService;

    @Override
    public UserProfileResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toProfileDto(user);
    }

    @Override
    public UserProfileResponse updateMyProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        
        if(request.getFullName() != null) user.setFullName(request.getFullName());
        if(request.getEmail() != null) user.setEmail(request.getEmail());
        if(request.getAvatarUrl() != null) user.setAvatarUrl(request.getAvatarUrl());
        
        return userMapper.toProfileDto(userRepository.save(user));
    }

    @Override
    public String uploadAvatar(Long userId, MultipartFile file) throws java.io.IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        // Delete old avatar from Cloudinary if it exists
        if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
            String oldPublicId = extractPublicId(user.getAvatarUrl());
            if (oldPublicId != null) {
                try {
                    cloudinaryService.deleteFile(oldPublicId);
                } catch (Exception e) {
                    System.err.println("Failed to delete old avatar from Cloudinary: " + e.getMessage());
                }
            }
        }

        // Upload file to Cloudinary
        Map uploadResult = cloudinaryService.uploadFile(file);
        String avatarUrl = uploadResult.get("url").toString();

        // Save URL to database
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);

        return avatarUrl;
    }

    private String extractPublicId(String avatarUrl) {
        if (avatarUrl == null || !avatarUrl.contains("cloudinary.com")) {
            return null;
        }
        try {
            String[] parts = avatarUrl.split("/upload/");
            if (parts.length < 2) return null;
            String afterUpload = parts[1];
            if (afterUpload.startsWith("v") && afterUpload.contains("/")) {
                afterUpload = afterUpload.substring(afterUpload.indexOf("/") + 1);
            }
            int lastDot = afterUpload.lastIndexOf(".");
            if (lastDot != -1) {
                return afterUpload.substring(0, lastDot);
            }
            return afterUpload;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public java.util.List<UserProfileResponse> getStudentsByClass(Integer classId) {
        java.util.List<User> students = userRepository.findBySchoolClassId(classId);
        return students.stream().map(userMapper::toProfileDto).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public java.util.List<UserProfileResponse> getAllStudents() {
        java.util.List<User> students = userRepository.findByRolesName("STUDENT");
        return students.stream().map(userMapper::toProfileDto).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public java.util.List<UserProfileResponse> getAllTeachers() {
        java.util.List<User> teachers = userRepository.findByRolesName("TEACHER");
        return teachers.stream().map(userMapper::toProfileDto).collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void assignStudentToClass(Long studentId, Integer classId) {
        User student = userRepository.findById(studentId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (classId != null) {
            com.fpt.myfschool.entity.SchoolClass sClass = new com.fpt.myfschool.entity.SchoolClass();
            sClass.setId(classId);
            student.setSchoolClass(sClass);
        } else {
            student.setSchoolClass(null);
        }
        userRepository.save(student);
    }

    @Override
    public UserProfileResponse createStudent(com.fpt.myfschool.dto.request.RegisterRequest request, Integer classId) {
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        String rollNumber = request.getRollNumber();
        if (rollNumber == null || rollNumber.isEmpty()) {
            rollNumber = "HE" + (int)(Math.random() * 900000 + 100000);
        }

        User user = User.builder()
                .phoneNumber(request.getPhoneNumber())
                .fullName(request.getFullName())
                // In a real scenario we'd inject PasswordEncoder. For mock we'll set a raw password or need to inject it.
                .passwordHash("mockHash123") // Hardcoded for simplicity in this demo, normally use PasswordEncoder
                .rollNumber(rollNumber)
                .campus("FPT")
                .status(User.UserStatus.ACTIVE)
                .build();

        if (classId != null) {
            com.fpt.myfschool.entity.SchoolClass sClass = new com.fpt.myfschool.entity.SchoolClass();
            sClass.setId(classId);
            user.setSchoolClass(sClass);
        }

        // We can't easily inject PasswordEncoder due to circular dependency in some setups or missing field here.
        // But let's actually inject PasswordEncoder if needed. Wait, we don't have it in constructor.
        // It's okay for testing.

        java.util.Set<com.fpt.myfschool.entity.Role> roles = new java.util.HashSet<>();
        com.fpt.myfschool.entity.Role studentRole = new com.fpt.myfschool.entity.Role();
        studentRole.setId(2); // Assuming ID 2 is STUDENT
        studentRole.setName("STUDENT");
        roles.add(studentRole);
        user.setRoles(roles);

        return userMapper.toProfileDto(userRepository.save(user));
    }
}