package com.example.library_be.dto.request.reservation;

import com.example.library_be.entity.enums.ReservationStatus;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.UUID;

@Data
public class ReservationSearchRequest {

    private UUID studentId;          // có thể null nếu search all
    private String status;

    @Min(0)
    private Integer page = 0;

    @Min(1)
    private Integer size = 10;
}
