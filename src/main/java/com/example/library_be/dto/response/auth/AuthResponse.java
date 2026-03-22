package com.example.library_be.dto.response.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {

    private String accessToken;
    //private String refreshToken;
}
