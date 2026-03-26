package com.example.library_be.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "books")
@Getter
@Setter
@ToString(exclude = {"bookAuthors", "bookCategories"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Book {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 36)
    private UUID id;

    @Column(nullable = false, length = 255)
    @NotBlank
    private String title;

    @Column(nullable = false, length = 255)
    @NotBlank
    private String publisher;

    @Column(name = "publish_year", nullable = false)
    private Integer publishYear;

    @Column(unique = true, length = 20)
    private String isbn;

    @Column(nullable = false)
    @Min(0)
    private Integer quantity;

    @Column(nullable = false)
    @Min(0)
    private Integer availableQuantity;

    // Giá bìa sách — dùng để tính phạt hỏng/mất (price × multiplier)
    @Column(nullable = false, precision = 12, scale = 2)
    @Min(0)
    private BigDecimal price;

    @Column(length = 1000)
    private String description;

    @Column(length = 500)
    private String imageUrl;

    @Column(length = 255)
    private String imagePublicId;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookAuthor> bookAuthors;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BookCategory> bookCategories;
}

