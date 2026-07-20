package com.fpt.myfschool.controller;

import com.fpt.myfschool.dto.request.LoginRequest;
import com.fpt.myfschool.dto.request.RegisterRequest;
import com.fpt.myfschool.dto.response.APIResponse;
import com.fpt.myfschool.dto.response.JwtResponse;
import com.fpt.myfschool.entity.Role;
import com.fpt.myfschool.entity.User;
import com.fpt.myfschool.exception.AppException;
import com.fpt.myfschool.exception.ErrorCode;
import com.fpt.myfschool.repository.RoleRepository;
import com.fpt.myfschool.repository.UserRepository;
import com.fpt.myfschool.security.JwtTokenProvider;
import com.fpt.myfschool.security.UserDetailsImpl;
import com.fpt.myfschool.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwtUtils;
    private final OtpService otpService;

    /**
     * [DÀNH CHO NGƯỜI DÙNG CHƯA ĐĂNG NHẬP]
     * API gửi mã OTP qua số điện thoại để đăng ký hoặc lấy lại mật khẩu
     */
    @PostMapping("/send-otp")
    public ResponseEntity<APIResponse<Void>> sendOtp(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String type = request.getOrDefault("type", "REGISTER"); // REGISTER or FORGOT_PASSWORD

        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return ResponseEntity.badRequest().body(APIResponse.<Void>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .code(400)
                    .message("Số điện thoại không được để trống")
                    .build());
        }

        boolean exists = userRepository.existsByPhoneNumber(phoneNumber);

        if ("REGISTER".equalsIgnoreCase(type) && exists) {
            return ResponseEntity.badRequest().body(APIResponse.<Void>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .code(400)
                    .message("Số điện thoại đã được đăng ký")
                    .build());
        }

        if ("FORGOT_PASSWORD".equalsIgnoreCase(type) && !exists) {
            return ResponseEntity.badRequest().body(APIResponse.<Void>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .code(404)
                    .message("Số điện thoại chưa được đăng ký trong hệ thống")
                    .build());
        }

        otpService.generateAndSendOtp(phoneNumber);
        
        return ResponseEntity.ok(APIResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .code(1000)
                .message("Đã gửi mã OTP (Xem trong console)")
                .build());
    }

    /**
     * [DÀNH CHO NGƯỜI DÙNG CHƯA ĐĂNG NHẬP]
     * API đăng nhập bằng số điện thoại và mật khẩu
     */
    @PostMapping("/login")
    public ResponseEntity<APIResponse<JwtResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getPhoneNumber(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();    
        String refreshToken = jwtUtils.generateRefreshToken(userDetails.getUsername());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        User user = userRepository.findById(userDetails.getId()).orElse(null);

        JwtResponse data = new JwtResponse(jwt, 
                                            refreshToken,
                                            userDetails.getId(), 
                                            userDetails.getUsername(), // this returns phoneNumber
                                            user != null ? user.getFullName() : "",
                                            user != null ? user.getAvatarUrl() : "",
                                            user != null ? user.getEmail() : "",
                                            user != null ? user.getRollNumber() : "",
                                            user != null ? user.getCampus() : "",
                                            user != null && user.getSchoolClass() != null ? user.getSchoolClass().getName() : "",
                                            roles);

        return ResponseEntity.ok(APIResponse.<JwtResponse>builder()
                .status(HttpStatus.OK.value())
                .code(1000)
                .message("Đăng nhập thành công")
                .data(data)
                .build());
    }

    /**
     * [DÀNH CHO NGƯỜI DÙNG CHƯA ĐĂNG NHẬP]
     * API làm mới Access Token bằng Refresh Token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<APIResponse<JwtResponse>> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().body(APIResponse.<JwtResponse>builder()
                    .status(HttpStatus.BAD_REQUEST.value()).code(400).message("Thiếu Refresh Token").build());
        }

        if (jwtUtils.validateJwtToken(refreshToken)) {
            String phoneNumber = jwtUtils.getUserNameFromJwtToken(refreshToken);
            User user = userRepository.findByPhoneNumber(phoneNumber).orElse(null);

            if (user != null) {
                // Generate new tokens
                Authentication authentication = new UsernamePasswordAuthenticationToken(UserDetailsImpl.build(user), null, UserDetailsImpl.build(user).getAuthorities());
                String newJwt = jwtUtils.generateJwtToken(authentication);
                String newRefreshToken = jwtUtils.generateRefreshToken(phoneNumber);

                List<String> roles = user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList());

                JwtResponse data = new JwtResponse(newJwt, newRefreshToken, user.getId(), user.getPhoneNumber(), user.getFullName(), user.getAvatarUrl(), user.getEmail(), user.getRollNumber(), user.getCampus(), user.getSchoolClass() != null ? user.getSchoolClass().getName() : "", roles);
                
                return ResponseEntity.ok(APIResponse.<JwtResponse>builder()
                        .status(HttpStatus.OK.value()).code(1000).message("Làm mới Token thành công").data(data).build());
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.<JwtResponse>builder()
                .status(HttpStatus.UNAUTHORIZED.value()).code(401).message("Refresh Token không hợp lệ hoặc đã hết hạn").build());
    }

    /**
     * [DÀNH CHO NGƯỜI DÙNG CHƯA ĐĂNG NHẬP]
     * API đăng ký tài khoản mới bằng số điện thoại và mã OTP
     */
    @PostMapping("/register")
    public ResponseEntity<APIResponse<Void>> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        if (userRepository.existsByPhoneNumber(signUpRequest.getPhoneNumber())) {
            // Ném thẳng Exception theo chuẩn mới thay vì tự return ResponseEntity
            throw new AppException(ErrorCode.USER_EXISTED); // Cần sửa sau thành PHONE_EXISTED nếu có
        }

        if (!otpService.verifyOtp(signUpRequest.getPhoneNumber(), signUpRequest.getOtp())) {
            return ResponseEntity.badRequest().body(APIResponse.<Void>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .code(400)
                    .message("Mã OTP không chính xác hoặc đã hết hạn")
                    .build());
        }

        String generatedRollNumber = signUpRequest.getRollNumber();
        if (generatedRollNumber == null || generatedRollNumber.isEmpty()) {
            generatedRollNumber = "HE" + (int)(Math.random() * 900000 + 100000);
        }

        String campus = signUpRequest.getCampus();
        if (campus == null || campus.isEmpty()) {
            campus = "FPT";
        }

        User user = User.builder()
                .phoneNumber(signUpRequest.getPhoneNumber())
                .fullName(signUpRequest.getFullName())
                .passwordHash(encoder.encode(signUpRequest.getPassword()))
                .rollNumber(generatedRollNumber)
                .campus(campus)
                .status(User.UserStatus.ACTIVE)
                .build();

        Set<Role> roles = new HashSet<>();
        Role userRole = roleRepository.findByName("STUDENT")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("STUDENT");
                    return roleRepository.save(newRole);
                });
                
        roles.add(userRole);
        user.setRoles(roles);
        
        userRepository.save(user);

        return ResponseEntity.ok(APIResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .code(1000)
                .message("Đăng ký thành công!")
                .build());
    }

    /**
     * [DÀNH CHO NGƯỜI DÙNG CHƯA ĐĂNG NHẬP]
     * API đặt lại mật khẩu mới thông qua mã OTP xác thực
     */
    @PostMapping("/reset-password")
    public ResponseEntity<APIResponse<Void>> resetPassword(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String otp = request.get("otp");
        String newPassword = request.get("newPassword");

        if (phoneNumber == null || otp == null || newPassword == null) {
            return ResponseEntity.badRequest().body(APIResponse.<Void>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .code(400)
                    .message("Dữ liệu không hợp lệ")
                    .build());
        }

        if (!otpService.verifyOtp(phoneNumber, otp)) {
            return ResponseEntity.badRequest().body(APIResponse.<Void>builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .code(400)
                    .message("Mã OTP không chính xác hoặc đã hết hạn")
                    .build());
        }

        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setPasswordHash(encoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok(APIResponse.<Void>builder()
                .status(HttpStatus.OK.value())
                .code(1000)
                .message("Đặt lại mật khẩu thành công!")
                .build());
    }

    /**
     * [DÀNH CHO NGƯỜI DÙNG CHƯA ĐĂNG NHẬP]
     * API Fake Đăng nhập bằng Google cho Admin/Teacher
     */
    @PostMapping("/google-login")
    public ResponseEntity<APIResponse<JwtResponse>> googleLogin(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(APIResponse.<JwtResponse>builder()
                    .status(HttpStatus.BAD_REQUEST.value()).code(400).message("Thiếu email").build());
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(APIResponse.<JwtResponse>builder()
                    .status(HttpStatus.UNAUTHORIZED.value()).code(401).message("Email chưa được đăng ký trong hệ thống").build());
        }

        // Bypass authentication check and generate tokens directly for fake Google Login
        Authentication authentication = new UsernamePasswordAuthenticationToken(UserDetailsImpl.build(user), null, UserDetailsImpl.build(user).getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(user.getPhoneNumber()); // using phone as standard username in this system

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        JwtResponse data = new JwtResponse(jwt, 
                                            refreshToken,
                                            user.getId(), 
                                            user.getPhoneNumber(), // this returns phoneNumber
                                            user.getFullName(),
                                            user.getAvatarUrl(),
                                            user.getEmail(),
                                            user.getRollNumber(),
                                            user.getCampus(),
                                            user.getSchoolClass() != null ? user.getSchoolClass().getName() : "",
                                            roles);

        return ResponseEntity.ok(APIResponse.<JwtResponse>builder()
                .status(HttpStatus.OK.value())
                .code(1000)
                .message("Đăng nhập bằng Google thành công")
                .data(data)
                .build());
    }
}
