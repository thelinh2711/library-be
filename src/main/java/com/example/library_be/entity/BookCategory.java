package com.example.library_be.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "book_categories")
@Getter
@Setter
@ToString(exclude = "book")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BookCategory {

    @Id
    @EqualsAndHashCode.Include
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 36)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookCategory)) return false;
        BookCategory that = (BookCategory) o;
        return Objects.equals(book, that.book) &&
                Objects.equals(category, that.category) &&
                Objects.equals(id, that.id); // thêm id vào so sánh
    }

    @Override
    public int hashCode() {
        return Objects.hash(book, category, id);
    }
}

