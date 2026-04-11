package com.example.library_be.dto.request.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @Schema(description = "Email người dùng", example = "user@example.com")
    @Email(message = "Email invalid")
    @NotBlank(message = "Email is required")
    private String email;

    @Schema(description = "Mật khẩu", example = "password123")
    @NotBlank(message = "Password is required")
    private String password;
}
