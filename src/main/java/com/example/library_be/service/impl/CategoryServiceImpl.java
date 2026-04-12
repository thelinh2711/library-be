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
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse create(CategoryCreateRequest request) {
        log.info("Creating category name={}", request.getName());

        if (categoryRepository.existsByName(request.getName())) {
            log.warn("Category already exists name={}", request.getName());
            throw new AppException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        Category category = categoryMapper.toEntity(request);
        Category saved = categoryRepository.save(category);

        log.info("Category created successfully id={}", saved.getId());
        return categoryMapper.toResponse(saved);
    }

    @Override
    public PageResponse<CategoryResponse> search(CategorySearchRequest request) {
        log.info("Searching categories keyword={}, page={}, size={}",
                request.getName(), request.getPage(), request.getSize());

        Pageable pageable = PageRequest.of(
                request.getPage(),
                request.getSize(),
                Sort.by(request.getSort(), "name")
        );

        Page<Category> page =
                categoryRepository.findByNameContainingIgnoreCase(request.getName(), pageable);

        log.debug("Found {} categories", page.getTotalElements());

        return PageResponse.from(page.map(categoryMapper::toResponse));
    }

    @Override
    public CategoryResponse getById(UUID id) {
        log.info("Get category by id={}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category not found id={}", id);
                    return new AppException(ErrorCode.CATEGORY_NOT_FOUND);
                });

        return categoryMapper.toResponse(category);
    }

    @Override
    public CategoryResponse update(UUID id, CategoryUpdateRequest request) {
        log.info("Updating category id={}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category not found for update id={}", id);
                    return new AppException(ErrorCode.CATEGORY_NOT_FOUND);
                });

        if (categoryRepository.existsByName(request.getName())
                && !category.getName().equals(request.getName())) {
            log.warn("Duplicate category name update attempt name={}", request.getName());
            throw new AppException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }

        categoryMapper.update(category, request);
        Category updated = categoryRepository.save(category);

        log.info("Category updated successfully id={}", id);

        return categoryMapper.toResponse(updated);
    }

    @Override
    public void delete(UUID id) {
        log.info("Deleting category id={}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Category not found for delete id={}", id);
                    return new AppException(ErrorCode.CATEGORY_NOT_FOUND);
                });

        try {
            categoryRepository.delete(category);
            log.info("Category deleted successfully id={}", id);
        } catch (DataIntegrityViolationException e) {
            log.error("Cannot delete category id={} because it is in use", id);
            throw new AppException(ErrorCode.CATEGORY_IN_USE);
        }
    }
}
