package com.example.library_be.entity;

import com.example.library_be.entity.enums.FineType;
import com.example.library_be.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "fines",
        indexes = {
                @Index(name = "idx_fine_borrow_item",    columnList = "borrow_item_id"),
                @Index(name = "idx_fine_payment_status", columnList = "payment_status")
        }
)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Fine {

    @EqualsAndHashCode.Include
    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 36)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrow_item_id", nullable = false)
    private BorrowItem borrowItem;

    // Policy đã áp dụng — lưu lại để audit dù sau này policy thay đổi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fine_policy_id")
    private FinePolicy finePolicy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FineType type;

    // Số tiền thực tế đã tính tại thời điểm tạo Fine
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    // Ghi chú thủ thư: mô tả tình trạng hỏng, lý do miễn giảm, v.v.
    @Column(length = 500)
    private String note;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.paymentStatus == null) {
            this.paymentStatus = PaymentStatus.UNPAID;
        }
    }
}
