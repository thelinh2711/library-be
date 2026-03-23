package com.example.library_be.dto.response.author;

import com.example.library_be.entity.enums.AuthorRole;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AuthorInfoResponse {
    private UUID id;
    private String name;
    private AuthorRole role;
}
