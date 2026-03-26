package com.example.library_be.entity;

import com.example.library_be.entity.enums.BorrowStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "borrow_records",
        indexes = {
                @Index(name = "idx_borrow_record_student", columnList = "student_id"),
                @Index(name = "idx_borrow_record_status",  columnList = "status")
        }
)
@Data
public class BorrowRecord {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 36)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    // Null nếu sinh viên mượn trực tiếp (không qua đặt trước)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", unique = true)
    private BookReservation reservation;

    @Column(name = "borrowed_at", nullable = false, updatable = false)
    private LocalDateTime borrowedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BorrowStatus status;

    // Ghi chú của thủ thư khi tạo phiếu (tình trạng sách lúc cho mượn, v.v.)
    @Column(name = "staff_note", length = 500)
    private String staffNote;

    @OneToMany(mappedBy = "borrowRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BorrowItem> items = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.borrowedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = BorrowStatus.BORROWING;
        }
    }
}
