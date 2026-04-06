package com.example.library_be.dto.request.auth;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank
    private String resetToken;

    @NotBlank
    @Size(min = 8, message = "Mật khẩu phải ít nhất 8 ký tự")
    private String newPassword;

    @NotBlank
    private String confirmPassword;

    @AssertTrue(message = "Mật khẩu xác nhận không khớp")
    public boolean isPasswordMatching() {
        if (newPassword == null || confirmPassword == null) return true; // để @NotBlank tự xử lý
        return newPassword.equals(confirmPassword);
    }
}
