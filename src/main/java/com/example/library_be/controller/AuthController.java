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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
@Tag(name = "Authentication", description = "API xác thực người dùng")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @Operation(summary = "Tạo tài khoản", description = "Admin tạo tài khoản cho người dùng")
    @PostMapping("/create-user")
    //@PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<UserResponse> createUser(@RequestBody @Valid RegisterRequest request) {
        return ApiResponse.success(authService.createUser(request));
    }

    @Operation(summary = "Đăng nhập", description = "Trả về accessToken, set refreshToken vào cookie")
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletResponse response, HttpServletRequest httpRequest
    ) {
        return ApiResponse.success(authService.login(request, response, httpRequest));
    }

    @Operation(summary = "Refresh token", description = "Lấy accessToken mới từ refreshToken trong cookie")
    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response, HttpServletRequest httpRequest
    ) {
        return ApiResponse.success(authService.refresh(refreshToken, response, httpRequest));
    }

    @Operation(summary = "Đăng xuất", description = "Xóa refreshToken khỏi cookie và invalidate token")
    @PostMapping("/logout")
    public ApiResponse<?> logout(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response, HttpServletRequest request
    ) {
        authService.logout(refreshToken, response, request);
        return ApiResponse.success(null);
    }

    @Operation(summary = "Đổi mật khẩu", description = "Người dùng đã đăng nhập đổi mật khẩu")
    @PutMapping("/change-password")
    public ApiResponse<String> changePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        authService.changePassword(userDetails.getUser().getId(), request);
        return ApiResponse.success("Password changed successfully");
    }

    @Operation(summary = "Quên mật khẩu", description = "Gửi OTP về email để xác thực đặt lại mật khẩu")
    @PostMapping("/forgot-password")
    public ApiResponse<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.sendOtp(request.getEmail());
        return ApiResponse.success("Mã OTP đã được gửi đến email của bạn");
    }

    @Operation(summary = "Xác thực OTP", description = "Xác minh OTP và trả về resetToken")
    @PostMapping("/verify-otp")
    public ApiResponse<Map<String, String>> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        String resetToken = passwordResetService.verifyOtp(request.getEmail(), request.getOtp());
        return ApiResponse.success(Map.of("resetToken", resetToken));
    }

    @Operation(summary = "Đặt lại mật khẩu", description = "Đặt lại mật khẩu bằng resetToken")
    @PostMapping("/reset-password")
    public ApiResponse<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getResetToken(), request.getNewPassword());
        return ApiResponse.success("Đặt lại mật khẩu thành công");
    }
}