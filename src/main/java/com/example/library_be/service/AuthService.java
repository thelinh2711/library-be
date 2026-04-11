package com.example.library_be.service;

import com.example.library_be.dto.request.auth.LoginRequest;
import com.example.library_be.dto.request.auth.RefreshTokenRequest;
import com.example.library_be.dto.request.user.ChangePasswordRequest;
import com.example.library_be.dto.request.user.RegisterRequest;
import com.example.library_be.dto.response.auth.AuthResponse;
import com.example.library_be.dto.response.user.UserResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.util.UUID;

public interface AuthService {
    UserResponse createUser(RegisterRequest request);

    // thêm HttpServletResponse để set cookie
    AuthResponse login(LoginRequest request, HttpServletResponse response);

    // nhận refreshToken từ cookie (String)
    AuthResponse refresh(String refreshToken, HttpServletResponse response);

    // logout dùng refreshToken từ cookie
    void logout(String refreshToken, HttpServletResponse response);

    void changePassword(UUID userId, ChangePasswordRequest request);
}

