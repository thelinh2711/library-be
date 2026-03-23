package com.example.library_be.entity;

import com.example.library_be.entity.enums.AuthorRole;
import jakarta.persistence.*;
import lombok.Data;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "book_authors")
@Getter
@Setter
@ToString(exclude = "book")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class BookAuthor {

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
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthorRole role;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookAuthor)) return false;
        BookAuthor that = (BookAuthor) o;
        return Objects.equals(book.getId(), that.book.getId()) &&
                Objects.equals(author.getId(), that.author.getId()) &&
                role == that.role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(book.getId(), author.getId(), role);
    }
}

