package com.example.library_be.mapper;

import com.example.library_be.dto.request.user.RegisterRequest;
import com.example.library_be.dto.response.user.UserResponse;
import com.example.library_be.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toUser(RegisterRequest request);

    UserResponse toUserResponse(User user);
}
