package com.example.library_be.repository;

import com.example.library_be.entity.BorrowItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface BorrowItemRepository extends JpaRepository<BorrowItem, UUID> {

    @Query("""
        SELECT i FROM BorrowItem i
        WHERE i.status = 'BORROWING' AND i.dueDate < :today
    """)
    List<BorrowItem> findOverdue(@Param("today") LocalDate today);

    @Modifying
    @Query("""
    UPDATE BorrowItem b
    SET b.status = 'OVERDUE'
    WHERE b.status = 'BORROWING'
      AND b.dueDate < :today
""")
    void markOverdue(LocalDate today);
}
