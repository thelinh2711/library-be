package com.example.library_be.controller;

import com.example.library_be.dto.request.book.BookCreateRequest;
import com.example.library_be.dto.request.book.BookSearchRequest;
import com.example.library_be.dto.request.book.BookUpdateRequest;
import com.example.library_be.dto.response.ApiResponse;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.book.BookDetailResponse;
import com.example.library_be.dto.response.book.BookResponse;
import com.example.library_be.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<BookResponse> create(@ModelAttribute @Valid BookCreateRequest request) {
        return ApiResponse.success(bookService.create(request));
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<BookResponse> update(@PathVariable UUID id, @ModelAttribute @Valid BookUpdateRequest request) {
        return ApiResponse.success(bookService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        bookService.delete(id);
        return ApiResponse.success("Xóa sách thành công", null);
    }

    @GetMapping
    public ApiResponse<PageResponse<BookResponse>> search(BookSearchRequest request) {
        return ApiResponse.success(bookService.search(request));
    }

    // GET DETAIL
    @GetMapping("/{id}")
    public ApiResponse<BookDetailResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(bookService.getById(id));
    }
}
