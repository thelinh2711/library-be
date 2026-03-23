package com.example.library_be.dto.response.book;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class BookResponse {

    private UUID id;
    private String title;
    private String publisher;
    private Integer publishYear;
    private String isbn;
    private Integer quantity;
    private Integer availableQuantity;
    private String description;
    private String imageUrl;
}