package com.example.library_be.service.impl;

import com.example.library_be.dto.request.category.CategoryCreateRequest;
import com.example.library_be.dto.request.category.CategorySearchRequest;
import com.example.library_be.dto.request.category.CategoryUpdateRequest;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.category.CategoryResponse;
import com.example.library_be.entity.Category;
import com.example.library_be.exception.AppException;
import com.example.library_be.exception.ErrorCode;
import com.example.library_be.mapper.CategoryMapper;
import com.example.library_be.repository.CategoryRepository;
import com.example.library_be.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse create(CategoryCreateRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        Category category = categoryMapper.toEntity(request);

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    public PageResponse<CategoryResponse> search(CategorySearchRequest request) {
        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(request.getSort(), "name")
        );

        Page<Category> page = categoryRepository
                .findByNameContainingIgnoreCase(request.getName(), pageable);

        return PageResponse.<CategoryResponse>builder()
                .content(page.getContent().stream()
                        .map(categoryMapper::toResponse)
                        .toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    public CategoryResponse getById(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        return categoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse update(UUID id, CategoryUpdateRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        // check name duplicate (trừ chính nó)
        if (categoryRepository.existsByName(request.getName())
                && !category.getName().equals(request.getName())) {
            throw new AppException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        categoryMapper.update(category, request);

        return categoryMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    public void delete(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.CATEGORY_NOT_FOUND));

        try {
            categoryRepository.delete(category);
        } catch (DataIntegrityViolationException e) {
            throw new AppException(ErrorCode.CATEGORY_IN_USE);
        }
    }
}
