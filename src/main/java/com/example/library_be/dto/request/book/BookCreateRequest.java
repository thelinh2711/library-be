package com.example.library_be.dto.request.book;

import com.example.library_be.entity.enums.AuthorRole;
import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
public class BookCreateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String publisher;

    private Integer publishYear;

    private String isbn;

    @Min(0)
    private Integer quantity;

    @Min(0)
    private Integer availableQuantity;

    private String description;
    private MultipartFile image;

    private List<AuthorItem> authors;
    private List<UUID> categoryIds;

    @Data
    public static class AuthorItem {
        private UUID authorId;
        private AuthorRole role;
    }
}
