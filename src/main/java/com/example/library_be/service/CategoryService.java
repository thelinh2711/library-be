package com.example.library_be.service;

import com.example.library_be.dto.request.category.CategoryCreateRequest;
import com.example.library_be.dto.request.category.CategorySearchRequest;
import com.example.library_be.dto.request.category.CategoryUpdateRequest;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.category.CategoryResponse;

import java.util.List;
import java.util.UUID;

public interface CategoryService {

    CategoryResponse create(CategoryCreateRequest request);

    //List<CategoryResponse> getAll();

    CategoryResponse getById(UUID id);

    CategoryResponse update(UUID id, CategoryUpdateRequest request);

    void delete(UUID id);

    public PageResponse<CategoryResponse> search(CategorySearchRequest request);
}
