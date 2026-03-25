package com.example.library_be.dto.request.book;

import lombok.Data;
import org.springframework.data.domain.Sort;

@Data
public class BookSearchRequest {
    private String keyword;
    private String category;
    private int page = 0;
    private int size = 10;
    private Sort.Direction sort = Sort.Direction.ASC;
}
