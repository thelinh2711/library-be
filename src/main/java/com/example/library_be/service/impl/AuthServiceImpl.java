package com.example.library_be.service.impl;

import com.example.library_be.dto.request.auth.LoginRequest;
import com.example.library_be.dto.request.auth.RefreshTokenRequest;
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
import com.example.library_be.security.JwtService;
import com.example.library_be.service.AuthService;
import com.example.library_be.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RedisService redisService;

    // ================= REGISTER =================
    @Override
    public UserResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        User user = userMapper.toUser(request);

        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.MEMBER);

        userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        // lưu Redis
        String key = "refresh_token:" + user.getId();
        redisService.save(key, refreshToken, jwtService.getRefreshExpiration());

        return authMapper.toAuthResponse(accessToken, refreshToken);
    }

    // ================= REFRESH =================
    @Override
    public AuthResponse refresh(RefreshTokenRequest request) {

        String refreshToken = request.getRefreshToken();

        if (!jwtService.isTokenValid(refreshToken) || !jwtService.isRefreshToken(refreshToken)) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String userId = jwtService.extractUserId(refreshToken);

        String key = "refresh_token:" + userId;
        String storedToken = redisService.get(key);

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new AppException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        User user = userRepository.findById(java.util.UUID.fromString(userId))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        redisService.save(key, newRefreshToken, jwtService.getRefreshExpiration());

        return authMapper.toAuthResponse(newAccessToken, newRefreshToken);
    }

    // ================= LOGOUT =================
    @Override
    public void logout(RefreshTokenRequest request) {

        String refreshToken = request.getRefreshToken();

        if (!jwtService.isTokenValid(refreshToken)) {
            return; // logout mềm
        }

        String userId = jwtService.extractUserId(refreshToken);

        redisService.delete("refresh_token:" + userId);
    }
}
