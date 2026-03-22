package com.example.library_be.dto.request.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryCreateRequest {

    @NotBlank
    private String name;

    private String description;
}