package com.example.library_be.repository;

import com.example.library_be.entity.Book;
import com.example.library_be.entity.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookCategoryRepository extends JpaRepository<BookCategory, UUID> {
    void deleteByBook(Book book);

}
