package com.example.library_be.entity;

import com.example.library_be.entity.enums.FineType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "fine_policies")
@Data
public class FinePolicy {

    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 36)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FineType type;

    // ── Dành cho LATE ──────────────────────────────────────────────────────────
    // Khung ngày: áp dụng từ ngày thứ dayFrom đến dayTo (null = không giới hạn)
    // Vd: dayFrom=1, dayTo=7  → ngày 1-7 mỗi ngày phạt amountPerDay
    //     dayFrom=8, dayTo=30 → ngày 8-30 mỗi ngày phạt amountPerDay
    //     dayFrom=31, dayTo=null → từ ngày 31 trở đi

    @Column(name = "day_from")
    private Integer dayFrom;

    @Column(name = "day_to")
    private Integer dayTo;

    @Column(name = "amount_per_day", precision = 12, scale = 2)
    private BigDecimal amountPerDay;

    // ── Dành cho DAMAGED / LOST ────────────────────────────────────────────────
    // Tiền phạt = book.price × multiplier
    // Vd: DAMAGED nhẹ → 0.3, DAMAGED nặng → 0.7, LOST → 1.5

    @Column(name = "multiplier", precision = 5, scale = 2)
    private BigDecimal multiplier;

    // Nhãn mức độ hỏng để thủ thư chọn (vd: "Hỏng nhẹ", "Hỏng nặng", "Mất sách")
    @Column(name = "damage_level", length = 50)
    private String damageLevel;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(length = 255)
    private String description;
}
