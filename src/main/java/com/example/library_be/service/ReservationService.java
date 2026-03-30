package com.example.library_be.service;

import com.example.library_be.dto.request.reservation.ReservationRequest;
import com.example.library_be.dto.request.reservation.ReservationSearchRequest;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.borrow.ReservationResponse;
import java.util.UUID;

public interface ReservationService {

    ReservationResponse create(UUID studentId, ReservationRequest request);

    ReservationResponse confirm(UUID reservationId);

    ReservationResponse cancel(UUID reservationId);

    PageResponse<ReservationResponse> search(ReservationSearchRequest request);

    void expireReservations();
}
