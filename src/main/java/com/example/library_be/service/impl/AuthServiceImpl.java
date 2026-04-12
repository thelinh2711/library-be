package com.example.library_be.service.impl;

import com.example.library_be.dto.request.auth.LoginRequest;
import com.example.library_be.dto.request.auth.RefreshTokenRequest;
import com.example.library_be.dto.request.user.ChangePasswordRequest;
import com.example.library_be.dto.request.user.RegisterRequest;
import com.example.library_be.dto.response.auth.AuthResponse;
import com.example.library_be.dto.response.user.UserResponse;
import com.example.library_be.entity.User;
import com.example.library_be.entity.enums.Role;
import com.example.library_be.exception.AppException;
import com.example.library_be.exception.ErrorCode;
import com.example.library_be.mapper.AuthMapper;
import com.example.library_be.mapper.UserMapper;
import com.example.library_be.repository.UserRepository;
import com.example.library_be.security.CustomUserDetails;
import com.example.library_be.security.JwtService;
import com.example.library_be.service.AuthService;
import com.example.library_be.service.RedisService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletResponse;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisService redisService;
    private final AuthenticationManager authenticationManager;

    // REGISTER
    @Override
    public UserResponse createUser(RegisterRequest request) {

        log.info("Register attempt with email={}", request.getEmail());

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            log.warn("Register failed: email already exists: {}", request.getEmail());
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.warn("Register failed: password mismatch for email={}", request.getEmail());
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.LIBRARIAN);

        userRepository.save(user);

        log.info("User registered successfully with email={}", request.getEmail());

        return userMapper.toUserResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        log.info("Login attempt with email={}", request.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Lấy user từ principal thay vì query DB lại
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        log.info("Login success for userId={}, email={}", user.getId(), user.getEmail());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // lưu Redis
        String key = "refresh_token:" + user.getId();
        redisService.save(key, refreshToken, jwtService.getRefreshExpiration());

        // set cookie
        addRefreshTokenCookie(response, refreshToken);

        return authMapper.toAuthResponse(accessToken);
    }

    // REFRESH
    @Override
    public AuthResponse refresh(String refreshToken, HttpServletResponse response) {
        log.info("Refresh token attempt");
        if (!jwtService.isTokenValid(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
            log.warn("Invalid refresh token");
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String userId = jwtService.extractUserId(refreshToken);
        log.info("Refreshing token for userId={}", userId);
        String key = "refresh_token:" + userId;
        String storedToken = redisService.get(key);

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        User user = userRepository.findById(java.util.UUID.fromString(userId))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        // update Redis
        redisService.save(key, newRefreshToken, jwtService.getRefreshExpiration());

        // set cookie mới
        addRefreshTokenCookie(response, newRefreshToken);

        return authMapper.toAuthResponse(newAccessToken);
    }

    // LOGOUT
    @Override
    public void logout(String refreshToken, HttpServletResponse response) {
        log.info("Logout attempt");
        if (!jwtService.isTokenValid(refreshToken)) {
            log.warn("Logout failed: invalid token");
            return;
        }

        String userId = jwtService.extractUserId(refreshToken);
        redisService.delete("refresh_token:" + userId);
        log.info("User logged out successfully userId={}", userId);
        // xoá cookie
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("refreshToken", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtService.getRefreshExpiration() / 1000));
        cookie.setSecure(false);

        response.addCookie(cookie);
    }

    @Override
    public void changePassword(UUID userId, ChangePasswordRequest request) {
        log.info("Change password attempt for userId={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found: userId={}", userId);
                    return new AppException(ErrorCode.USER_NOT_FOUND);
                });

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            log.warn("Change password failed: wrong old password for userId={}", userId);
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            log.warn("Change password failed: confirm mismatch for userId={}", userId);
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            log.warn("Change password failed: new password same as old for userId={}", userId);
            throw new AppException(ErrorCode.NEW_PASSWORD_DUPLICATE);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed successfully for userId={}", userId);
    }
}
