package com.example.library_be.repository;

import com.example.library_be.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, UUID> {
    @EntityGraph(attributePaths = {
            "bookAuthors.author",
            "bookCategories.category"
    })
    @Query("""
        SELECT DISTINCT b FROM Book b
        LEFT JOIN b.bookAuthors ba
        LEFT JOIN ba.author a
        LEFT JOIN b.bookCategories bc
        LEFT JOIN bc.category c
        WHERE
            (:keyword IS NULL OR
                LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(b.publisher) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
        AND (:category IS NULL OR LOWER(c.name) = LOWER(:category))
    """)
    Page<Book> searchBooks(
            @Param("keyword") String keyword,
            @Param("category") String category,
            Pageable pageable
    );

    @Query("""
    SELECT b FROM Book b
    LEFT JOIN FETCH b.bookAuthors ba
    LEFT JOIN FETCH ba.author
    LEFT JOIN FETCH b.bookCategories bc
    LEFT JOIN FETCH bc.category
    WHERE b.id = :id
""")
    Optional<Book> findDetailById(UUID id);
}
