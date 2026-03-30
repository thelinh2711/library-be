package com.example.library_be.repository;

import com.example.library_be.entity.BorrowRecord;
import com.example.library_be.entity.enums.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.UUID;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, UUID> {

    Page<BorrowRecord> findByStudentIdOrderByBorrowedAtDesc(UUID studentId, Pageable pageable);

    Page<BorrowRecord> findByStatusOrderByBorrowedAtDesc(BorrowStatus status, Pageable pageable);

    @Modifying
    @Query("""
    UPDATE BorrowRecord br
    SET br.status = com.example.library_be.entity.enums.BorrowStatus.OVERDUE
    WHERE br.status = com.example.library_be.entity.enums.BorrowStatus.BORROWING
      AND EXISTS (
        SELECT 1 FROM BorrowItem bi
        WHERE bi.borrowRecord = br
          AND bi.status = com.example.library_be.entity.enums.BorrowItemStatus.BORROWING
          AND bi.dueDate < :today
      )
""")
    void markOverdue(LocalDate today);

    @Query("""
        SELECT br FROM BorrowRecord br
        JOIN br.student s
        WHERE br.status = :status
          AND (
                LOWER(s.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(s.studentCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
          )
        ORDER BY br.borrowedAt DESC
    """)
    Page<BorrowRecord> searchByStatusAndKeyword(
            @Param("status") BorrowStatus status,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    Page<BorrowRecord> findAllByOrderByBorrowedAtDesc(Pageable pageable);
}
