package com.example.library_be.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity
@Table(name = "books")
@Data
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

    @Column(length = 1000)
    private String description;
}

