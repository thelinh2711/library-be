package com.example.library_be.repository;

import com.example.library_be.entity.Book;
import com.example.library_be.entity.BookAuthor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookAuthorRepository extends JpaRepository<BookAuthor, UUID> {
    void deleteByBook(Book book);
}
