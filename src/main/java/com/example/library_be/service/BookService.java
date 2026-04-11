package com.example.library_be.service;

import com.example.library_be.dto.request.book.BookCreateRequest;
import com.example.library_be.dto.request.book.BookSearchRequest;
import com.example.library_be.dto.request.book.BookUpdateRequest;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.book.BookDetailResponse;
import com.example.library_be.dto.response.book.BookResponse;
import org.springframework.data.domain.Page;

import java.util.UUID;

public interface BookService {

    BookResponse create(BookCreateRequest request);

    BookResponse update(UUID id, BookUpdateRequest request);

    void delete(UUID id);

    // search + pagination
    PageResponse<BookResponse> search(BookSearchRequest request);

    // get detail by id
    BookDetailResponse getById(UUID id);
}
