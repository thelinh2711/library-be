package com.example.library_be.entity;

import com.example.library_be.entity.enums.BorrowItemStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "borrow_items",
        indexes = {
                @Index(name = "idx_borrow_item_record", columnList = "borrow_record_id"),
                @Index(name = "idx_borrow_item_book",   columnList = "book_id"),
                @Index(name = "idx_borrow_item_status", columnList = "status")
        }
)
@Data
public class BorrowItem {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 36)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrow_record_id", nullable = false)
    private BorrowRecord borrowRecord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // Hạn trả riêng cho từng quyển
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    // Null nếu chưa trả
    @Column(name = "return_date")
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BorrowItemStatus status;

    // Một quyển có thể có nhiều Fine (vd: vừa trả muộn vừa làm hỏng)
    @OneToMany(mappedBy = "borrowItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Fine> fines = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.status == null) {
            this.status = BorrowItemStatus.BORROWING;
        }
    }
}