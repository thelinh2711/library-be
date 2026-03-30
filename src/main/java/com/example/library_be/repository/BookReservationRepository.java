package com.example.library_be.repository;

import com.example.library_be.entity.BookReservation;
import com.example.library_be.entity.enums.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface BookReservationRepository extends JpaRepository<BookReservation, UUID> {

    boolean existsByStudentIdAndBookIdAndStatusIn(
            UUID studentId, UUID bookId, List<ReservationStatus> statuses);

    Page<BookReservation> findByStatusOrderByReservedAtAsc(ReservationStatus status, Pageable pageable);

    Page<BookReservation> findByStudentIdOrderByReservedAtDesc(UUID studentId, Pageable pageable);

    @Query("SELECT r FROM BookReservation r WHERE r.status = 'CONFIRMED' AND r.expiredAt < :now")
    List<BookReservation> findExpired(@Param("now") LocalDateTime now);

    Page<BookReservation> findByStatusOrderByReservedAtDesc(
            ReservationStatus status,
            Pageable pageable
    );

    Page<BookReservation> findByStudentIdAndStatusOrderByReservedAtDesc(
            UUID studentId,
            ReservationStatus status,
            Pageable pageable
    );

    List<BookReservation> findAllByStatusAndExpiredAtBefore(
            ReservationStatus status,
            LocalDateTime time
    );

    Page<BookReservation> findByStudentIdAndStatusInOrderByReservedAtDesc(
            UUID studentId,
            List<ReservationStatus> statuses,
            Pageable pageable
    );

    Page<BookReservation> findByStatusInOrderByReservedAtDesc(
            List<ReservationStatus> statuses,
            Pageable pageable
    );
}
