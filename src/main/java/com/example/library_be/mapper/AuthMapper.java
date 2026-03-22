package com.example.library_be.mapper;

import com.example.library_be.dto.response.auth.AuthResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    default AuthResponse toAuthResponse(String accessToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .build();
    }
}