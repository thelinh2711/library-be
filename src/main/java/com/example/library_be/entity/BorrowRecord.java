package com.example.library_be.entity;

import com.example.library_be.entity.enums.BorrowStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "borrow_records",
        indexes = {
                @Index(name = "idx_borrow_record_student", columnList = "student_id"),
                @Index(name = "idx_borrow_record_status", columnList = "status")
        })
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class BorrowRecord extends BaseAuditable {

    @EqualsAndHashCode.Include
    @Id
    @UuidGenerator
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(length = 36)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", unique = true)
    private BookReservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BorrowStatus status;

    @Column(name = "staff_note", length = 500)
    private String staffNote;

    @OneToMany(mappedBy = "borrowRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BorrowItem> items = new LinkedHashSet<>();
}
