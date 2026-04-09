package com.example.library_be.controller;

import com.example.library_be.dto.request.book.BookCreateRequest;
import com.example.library_be.dto.request.book.BookSearchRequest;
import com.example.library_be.dto.request.book.BookUpdateRequest;
import com.example.library_be.dto.response.ApiResponse;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.book.BookDetailResponse;
import com.example.library_be.dto.response.book.BookResponse;
import com.example.library_be.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Book", description = "Quản lý sách")
public class BookController {

    private final BookService bookService;

    @Operation(summary = "Tạo sách", description = "Tạo mới sách - hỗ trợ upload file")
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<BookResponse> create(@ModelAttribute @Valid BookCreateRequest request) {
        return ApiResponse.success(bookService.create(request));
    }

    @Operation(summary = "Cập nhật sách", description = "Cập nhật thông tin sách - hỗ trợ upload file")
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<BookResponse> update(@PathVariable UUID id, @ModelAttribute @Valid BookUpdateRequest request) {
        return ApiResponse.success(bookService.update(id, request));
    }

    @Operation(summary = "Xóa sách", description = "Xóa sách theo ID (ADMIN)")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        bookService.delete(id);
        return ApiResponse.success("Xóa sách thành công", null);
    }

    @Operation(summary = "Tìm kiếm sách", description = "Phân trang, sắp xếp, tìm kiếm sách")
    @GetMapping
    public ApiResponse<PageResponse<BookResponse>> search(
            @Parameter(description = "Thông tin tìm kiếm, sắp xếp và phân trang")
            BookSearchRequest request) {
        return ApiResponse.success(bookService.search(request));
    }

    @Operation(summary = "Chi tiết sách", description = "Lấy thông tin chi tiết sách theo ID")
    @GetMapping("/{id}")
    public ApiResponse<BookDetailResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(bookService.getById(id));
    }
}
