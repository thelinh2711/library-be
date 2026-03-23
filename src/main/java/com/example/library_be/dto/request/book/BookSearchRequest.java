package com.example.library_be.dto.request.book;

import lombok.Data;

@Data
public class BookSearchRequest {
    private String keyword;   // title | author | publisher
    private String category;  // tên category
    private int page = 0;
    private int size = 10;
}
