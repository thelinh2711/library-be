package com.example.library_be.dto.request.book;

import com.example.library_be.entity.enums.AuthorRole;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class BookUpdateRequest {
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

    @NotNull
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal price;

    private String description;
    private MultipartFile image;

    private List<BookCreateRequest.AuthorItem> authors;
    private List<UUID> categoryIds;

    @NotNull
    private Long version;

    @Data
    public static class AuthorItem {
        private UUID authorId;
        private AuthorRole role;
    }
}
