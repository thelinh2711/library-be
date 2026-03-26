package com.example.library_be.entity;

import com.example.library_be.entity.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "book_reservations",
        indexes = {
                @Index(name = "idx_reservation_student", columnList = "student_id"),
                @Index(name = "idx_reservation_book",    columnList = "book_id"),
                @Index(name = "idx_reservation_status",  columnList = "status")
        }
)
@Data
public class BookReservation {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 36)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name = "reserved_at", nullable = false, updatable = false)
    private LocalDateTime reservedAt;

    // Thời hạn giữ chỗ — set sau khi thủ thư CONFIRMED (vd: +3 ngày)
    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReservationStatus status;

    // BorrowRecord được tạo khi sinh viên đến nhận sách
    @OneToOne(mappedBy = "reservation", fetch = FetchType.LAZY)
    private BorrowRecord borrowRecord;

    @PrePersist
    public void prePersist() {
        this.reservedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ReservationStatus.PENDING;
        }
    }
}
