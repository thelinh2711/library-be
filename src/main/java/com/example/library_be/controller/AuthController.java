package com.example.library_be.controller;

import com.example.library_be.dto.request.auth.*;
import com.example.library_be.dto.request.user.ChangePasswordRequest;
import com.example.library_be.dto.request.user.RegisterRequest;
import com.example.library_be.dto.response.ApiResponse;
import com.example.library_be.dto.response.auth.AuthResponse;
import com.example.library_be.dto.response.user.UserResponse;
import com.example.library_be.entity.User;
import com.example.library_be.security.CustomUserDetails;
import com.example.library_be.service.AuthService;
import com.example.library_be.service.PasswordResetService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/create-user")
    //@PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid RegisterRequest request) {
        return ApiResponse.success(authService.createUser(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletResponse response
    ) {
        return ApiResponse.success(authService.login(request, response));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response
    ) {
        return ApiResponse.success(authService.refresh(refreshToken, response));
    }

    @PostMapping("/logout")
    public ApiResponse<?> logout(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response
    ) {
        authService.logout(refreshToken, response);
        return ApiResponse.success(null);
    }

    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        authService.changePassword(userDetails.getUser().getId(), request);
        return ApiResponse.success("Password changed successfully");
    }

    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.sendOtp(request.getEmail());
        return ApiResponse.success("Mã OTP đã được gửi đến email của bạn");
    }

    @PostMapping("/verify-otp")
    public ApiResponse<Map<String, String>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        String resetToken = passwordResetService.verifyOtp(request.getEmail(), request.getOtp());
        return ApiResponse.success(Map.of("resetToken", resetToken));
    }

    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getResetToken(), request.getNewPassword());
        return ApiResponse.success("Đặt lại mật khẩu thành công");
    }
}