package com.example.library_be.repository;

import com.example.library_be.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface StudentRepository extends JpaRepository<Student, UUID> {
    boolean existsByStudentCode(String studentCode);

    @Query("""
        SELECT s FROM Student s
        WHERE
            (:keyword IS NULL OR
                LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(s.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
                LOWER(s.studentCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
            )
        AND (:className IS NULL OR LOWER(s.className) = LOWER(:className))
        AND (:faculty IS NULL OR LOWER(s.faculty) = LOWER(:faculty))
    """)
    Page<Student> searchStudents(
            @Param("keyword") String keyword,
            @Param("className") String className,
            @Param("faculty") String faculty,
            Pageable pageable
    );

    @Query("SELECT DISTINCT s.faculty FROM Student s WHERE s.faculty IS NOT NULL ORDER BY s.faculty")
    List<String> findDistinctFaculties();

    @Query("SELECT DISTINCT s.className FROM Student s WHERE s.className IS NOT NULL ORDER BY s.className")
    List<String> findDistinctClasses();

    @Query("SELECT DISTINCT s.className FROM Student s WHERE s.faculty = :faculty AND s.className IS NOT NULL ORDER BY s.className")
    List<String> findDistinctClassesByFaculty(@Param("faculty") String faculty);

}
