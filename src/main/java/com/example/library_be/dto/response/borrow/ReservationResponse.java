package com.example.library_be.dto.response.borrow;

import com.example.library_be.entity.enums.ReservationStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ReservationResponse {
    private UUID id;
    private UUID studentId;
    private String studentName;
    private UUID bookId;
    private String bookTitle;
    private ReservationStatus status;
    private LocalDateTime reservedAt;
    private LocalDateTime expiredAt;
}
