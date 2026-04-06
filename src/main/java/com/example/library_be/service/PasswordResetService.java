package com.example.library_be.service;

public interface PasswordResetService {
    void sendOtp(String email);
    String verifyOtp(String email, String otp);
    void resetPassword(String resetToken, String newPassword);
}
