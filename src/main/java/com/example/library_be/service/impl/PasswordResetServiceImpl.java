package com.example.library_be.service.impl;

import com.example.library_be.entity.User;
import com.example.library_be.repository.UserRepository;
import com.example.library_be.service.PasswordResetService;
import com.example.library_be.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final RedisService redisService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;

    @Value("${otp.expiration-ms}")
    private long otpExpirationMs;          // 5 phút

    @Value("${otp.reset-token-expiration-ms}")
    private long resetTokenExpirationMs;   // 10 phút

    @Value("${otp.max-attempts}")
    private int maxAttempts;               // 3 lần

    @Value("${otp.resend-limit}")
    private int resendLimit;               // 3 lần/giờ

    @Value("${otp.resend-limit-expiration-ms}")
    private long resendLimitExpirationMs;  // 1 giờ

    @Value("${otp.lock-expiration-ms}")
    private long lockExpirationMs;         // 15 phút

    // Redis key prefix
    private static final String OTP_PREFIX      = "otp:";
    private static final String RESET_PREFIX    = "reset:";
    private static final String ATTEMPTS_PREFIX = "otp_attempts:";
    private static final String LIMIT_PREFIX    = "otp_limit:";
    private static final String LOCK_PREFIX     = "otp_lock:";

    // BƯỚC 1: Gửi OTP
    @Override
    public void sendOtp(String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        // Kiểm tra đang bị khóa không
        if (redisService.exists(LOCK_PREFIX + email)) {
            throw new RuntimeException("Tài khoản tạm thời bị khóa, vui lòng thử lại sau 15 phút");
        }

        // Kiểm tra giới hạn gửi OTP
        String limitStr = redisService.get(LIMIT_PREFIX + email);
        int sendCount = limitStr != null ? Integer.parseInt(limitStr) : 0;

        if (sendCount >= resendLimit) {
            throw new RuntimeException("Bạn đã gửi OTP quá " + resendLimit + " lần, vui lòng thử lại sau 1 giờ");
        }

        // Tăng counter gửi OTP
        if (sendCount == 0) {
            // Lần đầu → set với TTL 1 giờ
            redisService.save(LIMIT_PREFIX + email, "1", resendLimitExpirationMs);
        } else {
            // Lần sau → tăng lên (TTL giữ nguyên từ lần đầu)
            redisService.save(LIMIT_PREFIX + email,
                    String.valueOf(sendCount + 1),
                    resendLimitExpirationMs);
        }

        // Tạo OTP 6 số
        String otp = String.format("%06d", new Random().nextInt(999999));

        // Lưu OTP vào Redis
        redisService.save(OTP_PREFIX + email, otp, otpExpirationMs);

        // Reset số lần nhập sai (nếu gửi OTP mới)
        redisService.delete(ATTEMPTS_PREFIX + email);

        // Gửi email
        sendOtpEmail(email, otp);
    }

    // BƯỚC 2: Verify OTP
    @Override
    public String verifyOtp(String email, String otp) {

        // Kiểm tra đang bị khóa không
        if (redisService.exists(LOCK_PREFIX + email)) {
            throw new RuntimeException("Tài khoản tạm thời bị khóa, vui lòng thử lại sau 15 phút");
        }

        // Kiểm tra OTP còn tồn tại không
        String savedOtp = redisService.get(OTP_PREFIX + email);
        if (savedOtp == null) {
            throw new RuntimeException("OTP đã hết hạn, vui lòng gửi lại");
        }

        // Kiểm tra OTP có đúng không
        if (!savedOtp.equals(otp)) {
            handleWrongOtp(email);  // tăng số lần sai, có thể lock
            return null;
        }

        // OTP đúng → xóa OTP + attempts
        redisService.delete(OTP_PREFIX + email);
        redisService.delete(ATTEMPTS_PREFIX + email);

        // Tạo reset token
        String resetToken = UUID.randomUUID().toString();
        redisService.save(RESET_PREFIX + resetToken, email, resetTokenExpirationMs);

        return resetToken;
    }

    // BƯỚC 3: Đặt lại mật khẩu
    @Override
    public void resetPassword(String resetToken, String newPassword) {

        // Kiểm tra reset token
        String email = redisService.get(RESET_PREFIX + resetToken);
        if (email == null) {
            throw new RuntimeException("Phiên đặt lại mật khẩu đã hết hạn, vui lòng xác minh OTP lại");
        }

        // Đổi mật khẩu
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Xóa reset token → không dùng lại được
        redisService.delete(RESET_PREFIX + resetToken);

        // Xóa refresh token → buộc login lại trên tất cả thiết bị
        redisService.delete("refresh_token:" + user.getId());
    }

    // PRIVATE
    private void handleWrongOtp(String email) {
        String attemptsStr = redisService.get(ATTEMPTS_PREFIX + email);
        int attempts = attemptsStr != null ? Integer.parseInt(attemptsStr) : 0;
        attempts++;

        int remaining = maxAttempts - attempts;

        if (attempts >= maxAttempts) {
            // Khóa tài khoản 15 phút
            redisService.delete(OTP_PREFIX + email);
            redisService.delete(ATTEMPTS_PREFIX + email);
            redisService.save(LOCK_PREFIX + email, "locked", lockExpirationMs);
            throw new RuntimeException("Bạn đã nhập sai quá " + maxAttempts + " lần, tài khoản bị khóa 15 phút");
        }

        // Lưu số lần sai
        redisService.save(ATTEMPTS_PREFIX + email,
                String.valueOf(attempts),
                otpExpirationMs); // TTL bằng OTP để tự xóa cùng lúc

        throw new RuntimeException("OTP không đúng, bạn còn " + remaining + " lần thử");
    }

    private void sendOtpEmail(String toEmail, String otp) {
        System.out.println(">>> DEBUG EMAIL <<<");
        System.out.println("To: " + toEmail);
        System.out.println("OTP: " + otp);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setFrom("linhkhoai1008@gmail.com");
        message.setSubject("Mã OTP đặt lại mật khẩu");
        message.setText(
                "Mã OTP của bạn là: " + otp + "\n" +
                        "Mã có hiệu lực trong 5 phút.\n" +
                        "Không chia sẻ mã này với ai."
        );
        mailSender.send(message);
    }
}