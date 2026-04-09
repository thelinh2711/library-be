package com.example.library_be.controller;

import com.example.library_be.dto.request.author.AuthorCreateRequest;
import com.example.library_be.dto.request.author.AuthorSearchRequest;
import com.example.library_be.dto.request.author.AuthorUpdateRequest;
import com.example.library_be.dto.response.ApiResponse;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.author.AuthorResponse;
import com.example.library_be.service.AuthorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
@Tag(name = "Author", description = "Quản lý tác giả")
public class AuthorController {

    private final AuthorService authorService;

    @Operation(summary = "Tạo tác giả", description = "Tạo mới tác giả")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<AuthorResponse> create(@RequestBody @Valid AuthorCreateRequest request) {
        return ApiResponse.success(authorService.create(request));
    }

    @Operation(summary = "Lấy danh sách tác giả", description = "Phân trang, tìm kiếm và sắp xếp tác giả")
    @GetMapping
    public ApiResponse<PageResponse<AuthorResponse>> getAll(
            @Parameter(description = "Thông tin tìm kiếm, sắp xếp và phân trang")
            @Valid AuthorSearchRequest request) {
        return ApiResponse.success(authorService.getAll(request));
    }

    @Operation(summary = "Lấy chi tiết tác giả", description = "Lấy thông tin tác giả theo ID")
    @GetMapping("/{id}")
    public ApiResponse<AuthorResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(authorService.getById(id));
    }

    @Operation(summary = "Cập nhật tác giả", description = "Cập nhật thông tin tác giả")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<AuthorResponse> update(@PathVariable UUID id, @RequestBody @Valid AuthorUpdateRequest request) {
        return ApiResponse.success(authorService.update(id, request));
    }

    @Operation(summary = "Xóa tác giả", description = "Xóa tác giả theo ID")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        authorService.delete(id);
        return ApiResponse.success("Xóa tác giả thành công", null);
    }
}
