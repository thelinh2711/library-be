package com.example.library_be.repository;

import com.example.library_be.entity.Fine;
import com.example.library_be.entity.enums.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface FineRepository extends JpaRepository<Fine, UUID> {

    @Query("""
        SELECT COUNT(f) > 0 FROM Fine f
        WHERE f.borrowItem.borrowRecord.student.id = :studentId
          AND f.paymentStatus = 'UNPAID'
    """)
    boolean hasUnpaidFine(@Param("studentId") UUID studentId);

    @Query("""
        SELECT f FROM Fine f
        WHERE f.borrowItem.borrowRecord.student.id = :studentId
        ORDER BY f.createdAt DESC
    """)
    Page<Fine> findByStudentId(@Param("studentId") UUID studentId, Pageable pageable);

    Page<Fine> findByPaymentStatusOrderByCreatedAtDesc(PaymentStatus status, Pageable pageable);

    // Tất cả fine + search theo tên / mã SV + filter payment status
    @Query("""
            SELECT f FROM Fine f
            JOIN f.borrowItem bi
            JOIN bi.borrowRecord br
            JOIN br.student s
            WHERE (:status IS NULL OR f.paymentStatus = :status)
              AND (
                    :keyword IS NULL OR :keyword = ''
                    OR LOWER(s.fullName)   LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(s.studentCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  )
            ORDER BY f.createdAt DESC
            """)
    Page<Fine> searchFines(
            @Param("status")  PaymentStatus status,
            @Param("keyword") String keyword,
            Pageable pageable);
}
