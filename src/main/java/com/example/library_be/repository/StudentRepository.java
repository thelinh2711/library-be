package com.example.library_be.repository;

import com.example.library_be.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID> {
    boolean existsByStudentCode(String studentCode);

}
