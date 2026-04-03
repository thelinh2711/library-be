package com.example.library_be.controller;

import com.example.library_be.dto.request.reservation.ReservationRequest;
import com.example.library_be.dto.request.reservation.ReservationSearchRequest;
import com.example.library_be.dto.response.ApiResponse;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.borrow.ReservationResponse;
import com.example.library_be.entity.User;
import com.example.library_be.security.CustomUserDetails;
import com.example.library_be.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    // STUDENT: đặt trước
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<ReservationResponse> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ReservationRequest request) {

        return ApiResponse.success(
                reservationService.create(userDetails.getUserId(), request)
        );
    }

    // STUDENT: xem của mình
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<PageResponse<ReservationResponse>> getMy(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @ModelAttribute ReservationSearchRequest request) {

        // chống hack studentId từ FE
        request.setStudentId(userDetails.getUserId());

        return ApiResponse.success(
                reservationService.search(request)
        );
    }

    // ADMIN / LIBRARIAN: search (thay cho getByStatus)
    @GetMapping("/search")
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ApiResponse<PageResponse<ReservationResponse>> search(@ModelAttribute ReservationSearchRequest request) {

        return ApiResponse.success(
                reservationService.search(request)
        );
    }

    // ADMIN: xem theo sinh viên
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ApiResponse<PageResponse<ReservationResponse>> getByStudent(@PathVariable UUID studentId,
            @ModelAttribute ReservationSearchRequest request) {

        request.setStudentId(studentId);

        return ApiResponse.success(
                reservationService.search(request)
        );
    }

    // CANCEL
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('STUDENT') or hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ApiResponse<ReservationResponse> cancel(@PathVariable UUID id) {

        return ApiResponse.success(
                reservationService.cancel(id)
        );
    }

    // CONFIRM
    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ApiResponse<ReservationResponse> confirm(@PathVariable UUID id) {

        return ApiResponse.success(
                reservationService.confirm(id)
        );
    }
}