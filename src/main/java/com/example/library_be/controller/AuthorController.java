package com.example.library_be.controller;

import com.example.library_be.dto.request.author.AuthorCreateRequest;
import com.example.library_be.dto.request.author.AuthorUpdateRequest;
import com.example.library_be.dto.response.ApiResponse;
import com.example.library_be.dto.response.author.AuthorResponse;
import com.example.library_be.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<AuthorResponse> create(@RequestBody @Valid AuthorCreateRequest request) {
        return ApiResponse.success(authorService.create(request));
    }

    @GetMapping
    public ApiResponse<List<AuthorResponse>> getAll() {
        return ApiResponse.success(authorService.getAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<AuthorResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(authorService.getById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<AuthorResponse> update(@PathVariable UUID id, @RequestBody @Valid AuthorUpdateRequest request) {
        return ApiResponse.success(authorService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        authorService.delete(id);
        return ApiResponse.success("Xóa tác giả thành công", null);
    }
}
