package com.example.library_be.dto.request.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @Email(message = "INVALID_KEY")
    @NotBlank(message = "INVALID_KEY")
    private String email;

    @NotBlank(message = "INVALID_KEY")
    @Size(min = 8, message = "PASSWORD_TOO_SHORT")
    private String password;

    @NotBlank(message = "INVALID_KEY")
    private String confirmPassword;

    @NotBlank(message = "INVALID_KEY")
    private String fullName;
}
