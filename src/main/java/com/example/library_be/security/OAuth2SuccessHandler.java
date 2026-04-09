package com.example.library_be.security;

import com.example.library_be.entity.User;
import com.example.library_be.service.RedisService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final RedisService redisService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // Lấy internal user từ CustomUserDetails
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        // Generate + lưu token — y hệt login thường
        String accessToken  = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        String key = "refresh_token:" + user.getId();
        redisService.save(key, refreshToken, jwtService.getRefreshExpiration());

        // Set cookie — y hệt login thường
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtService.getRefreshExpiration() / 1000));
        cookie.setSecure(false);
        response.addCookie(cookie);

        // Redirect về FE kèm accessToken
        response.sendRedirect(frontendUrl + "/auth/callback?token=" + accessToken);
    }
}
