package com.example.library_be.dto.response.book;

import com.example.library_be.dto.response.author.AuthorInfoResponse;
import com.example.library_be.dto.response.category.CategoryResponse;
import com.example.library_be.entity.enums.AuthorRole;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class BookDetailResponse {
    private UUID id;
    private String title;
    private String publisher;
    private Integer publishYear;
    private String isbn;
    private Integer quantity;
    private Integer availableQuantity;
    private String description;
    private String imageUrl;

    private List<AuthorInfoResponse> authors;
    private List<CategoryResponse> categories;
}


