package com.example.library_be.repository;

import com.example.library_be.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuthorRepository extends JpaRepository<Author, UUID> {
}
