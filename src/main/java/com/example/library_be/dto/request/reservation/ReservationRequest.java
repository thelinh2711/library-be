package com.example.library_be.dto.request.reservation;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.UUID;

@Data
public class ReservationRequest {
    @NotNull(message = "Mã sách không được để trống")
    private UUID bookId;
}
