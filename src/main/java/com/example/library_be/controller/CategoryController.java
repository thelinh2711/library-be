package com.example.library_be.controller;

import com.example.library_be.dto.request.category.CategoryCreateRequest;
import com.example.library_be.dto.request.category.CategorySearchRequest;
import com.example.library_be.dto.request.category.CategoryUpdateRequest;
import com.example.library_be.dto.response.ApiResponse;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.category.CategoryResponse;
import com.example.library_be.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Quản lý thể loại sách")
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "Tạo thể loại", description = "Tạo mới thể loại sách")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<CategoryResponse> create(@RequestBody @Valid CategoryCreateRequest request) {
        return ApiResponse.success(categoryService.create(request));
    }

    @Operation(summary = "Danh sách thể loại", description = "Phân trang, sort, tìm kiếm thể loại sách")
    @GetMapping
    public ApiResponse<PageResponse<CategoryResponse>> getCategories(@Valid CategorySearchRequest request) {
        return ApiResponse.success(categoryService.search(request));
    }

    @Operation(summary = "Chi tiết thể loại", description = "Lấy thông tin thể loại theo ID")
    @GetMapping("/{id}")
    public ApiResponse<CategoryResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(categoryService.getById(id));
    }

    @Operation(summary = "Cập nhật thể loại", description = "Cập nhật thể loại")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<CategoryResponse> update(@PathVariable UUID id, @RequestBody @Valid CategoryUpdateRequest request) {
        return ApiResponse.success(categoryService.update(id, request));
    }

    @Operation(summary = "Xóa thể loại", description = "Xóa thể loại theo ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        categoryService.delete(id);
        return ApiResponse.success("Xóa thành công", null);
    }
}
