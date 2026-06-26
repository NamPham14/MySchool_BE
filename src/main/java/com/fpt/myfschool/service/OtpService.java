package com.fpt.myfschool.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    // Store OTPs in memory for dev purposes. Key: phoneNumber, Value: OTP
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public String generateAndSendOtp(String phoneNumber) {
        // Generate 6-digit OTP
        String otp = String.format("%06d", random.nextInt(1000000));
        
        // Save to storage
        otpStorage.put(phoneNumber, otp);
        
        // Mock sending SMS by printing to console
        System.out.println("=================================================");
        System.out.println("MOCK SMS: Gửi OTP tới số điện thoại " + phoneNumber);
        System.out.println("Mã OTP của bạn là: " + otp);
        System.out.println("=================================================");
        
        return otp;
    }

    public boolean verifyOtp(String phoneNumber, String otp) {
        String storedOtp = otpStorage.get(phoneNumber);
        if (storedOtp != null && storedOtp.equals(otp)) {
            // Remove after successful verification
            otpStorage.remove(phoneNumber);
            return true;
        }
        return false;
    }
}
