package com.example.library_be.dto.request.author;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AuthorUpdateRequest {

    @NotBlank
    private String name;

    private LocalDate dateOfBirth;

    private String bio;
}
