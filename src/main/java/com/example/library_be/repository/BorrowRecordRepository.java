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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, UUID> {

    // ── Single record ─────────────────────────────────────────────────────────
    @Query("""
        SELECT r FROM BorrowRecord r
        LEFT JOIN FETCH r.student
        LEFT JOIN FETCH r.reservation
        LEFT JOIN FETCH r.items i
        LEFT JOIN FETCH i.book
        WHERE r.id = :id
        """)
    Optional<BorrowRecord> findByIdWithItems(@Param("id") UUID id);

    @Query("""
        SELECT r FROM BorrowRecord r
        LEFT JOIN FETCH r.items i
        LEFT JOIN FETCH i.fines f
        LEFT JOIN FETCH f.finePolicy
        WHERE r.id = :id
        """)
    Optional<BorrowRecord> findByIdWithFines(@Param("id") UUID id);

    // ── Batch fetch ───────────────────────────────────────────────────────────
    @Query("""
        SELECT DISTINCT r FROM BorrowRecord r
        LEFT JOIN FETCH r.student
        LEFT JOIN FETCH r.reservation
        LEFT JOIN FETCH r.items i
        LEFT JOIN FETCH i.book
        WHERE r.id IN :ids
        """)
    List<BorrowRecord> fetchWithItems(@Param("ids") List<UUID> ids);

    @Query("""
        SELECT DISTINCT r FROM BorrowRecord r
        LEFT JOIN FETCH r.items i
        LEFT JOIN FETCH i.fines f
        LEFT JOIN FETCH f.finePolicy
        WHERE r.id IN :ids
        """)
    List<BorrowRecord> fetchWithFines(@Param("ids") List<UUID> ids);

    // ── Pagination IDs ────────────────────────────────────────────────────────
    @Query("SELECT r.id FROM BorrowRecord r WHERE r.student.id = :studentId ORDER BY r.borrowedAt DESC")
    Page<UUID> findIdsByStudentId(@Param("studentId") UUID studentId, Pageable pageable);

    @Query("SELECT r.id FROM BorrowRecord r WHERE r.status = :status ORDER BY r.borrowedAt DESC")
    Page<UUID> findIdsByStatus(@Param("status") BorrowStatus status, Pageable pageable);

    @Query("""
        SELECT r.id FROM BorrowRecord r
        JOIN r.student s
        WHERE r.status = :status
          AND (LOWER(s.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(s.studentCode) LIKE LOWER(CONCAT('%', :keyword, '%')))
        ORDER BY r.borrowedAt DESC
        """)
    Page<UUID> findIdsByStatusAndKeyword(@Param("status") BorrowStatus status, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT r.id FROM BorrowRecord r ORDER BY r.borrowedAt DESC")
    Page<UUID> findAllIds(Pageable pageable);

    // ── Scheduled job ─────────────────────────────────────────────────────────
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
    void markOverdue(@Param("today") LocalDate today);
}
