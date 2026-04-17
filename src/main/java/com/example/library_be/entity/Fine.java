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
@Table(name = "fines",
        indexes = {
                @Index(name = "idx_fine_borrow_item", columnList = "borrow_item_id"),
                @Index(name = "idx_fine_payment_status", columnList = "payment_status")
        })
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Fine extends BaseAuditable {

    @EqualsAndHashCode.Include
    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 36)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrow_item_id", nullable = false)
    private BorrowItem borrowItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fine_policy_id")
    private FinePolicy finePolicy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FineType type;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(length = 500)
    private String note;
}
