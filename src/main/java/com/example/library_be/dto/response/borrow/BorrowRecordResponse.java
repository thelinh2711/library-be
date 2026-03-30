package com.example.library_be.dto.response.borrow;

import com.example.library_be.entity.enums.*;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class BorrowRecordResponse {
    private UUID id;
    private UUID studentId;
    private String studentName;
    private UUID reservationId;
    private BorrowStatus status;
    private LocalDateTime borrowedAt;
    private String staffNote;
    private List<BorrowItemResponse> items;

    @Data
    @Builder
    public static class BorrowItemResponse {
        private UUID id;
        private UUID bookId;
        private String bookTitle;
        private LocalDate dueDate;
        private LocalDate returnDate;
        private BorrowItemStatus status;
        private List<FineResponse> fines;
    }

    @Data
    @Builder
    public static class FineResponse {
        private UUID id;
        private FineType type;
        private BigDecimal amount;
        private PaymentStatus paymentStatus;
        private LocalDateTime paidAt;
        private String note;
        // Thêm để FE hiển thị không cần flatten
        private UUID studentId;
        private String studentName;
        private String studentCode;
        private UUID bookId;
        private String bookTitle;
    }
}
