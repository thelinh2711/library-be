package com.example.library_be.dto.response.author;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class AuthorResponse {

    private UUID id;
    private String name;
    private LocalDate dateOfBirth;
    private String bio;
}
