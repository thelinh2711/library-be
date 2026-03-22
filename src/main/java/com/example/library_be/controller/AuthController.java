package com.example.library_be.controller;

import com.example.library_be.dto.request.auth.LoginRequest;
import com.example.library_be.dto.request.auth.RefreshTokenRequest;
import com.example.library_be.dto.request.user.RegisterRequest;
import com.example.library_be.dto.response.ApiResponse;
import com.example.library_be.dto.response.auth.AuthResponse;
import com.example.library_be.dto.response.user.UserResponse;
import com.example.library_be.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<UserResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
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

}