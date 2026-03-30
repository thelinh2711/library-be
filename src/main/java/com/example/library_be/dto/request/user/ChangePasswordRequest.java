package com.example.library_be.dto.request.user;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String oldPassword;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String newPassword;

    @Size(min = 8, message = "Password must be at least 8 characters")
    private String confirmPassword;
}
