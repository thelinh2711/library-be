package com.example.library_be.mapper;

import com.example.library_be.dto.request.category.CategoryCreateRequest;
import com.example.library_be.dto.request.category.CategoryUpdateRequest;
import com.example.library_be.dto.response.category.CategoryResponse;
import com.example.library_be.entity.Category;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toEntity(CategoryCreateRequest request);

    CategoryResponse toResponse(Category category);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(@MappingTarget Category category, CategoryUpdateRequest request);
}
