package com.example.library_be.controller;

import com.example.library_be.dto.request.reservation.ReservationRequest;
import com.example.library_be.dto.request.reservation.ReservationSearchRequest;
import com.example.library_be.dto.response.ApiResponse;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.borrow.ReservationResponse;
import com.example.library_be.entity.User;
import com.example.library_be.security.CustomUserDetails;
import com.example.library_be.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Reservation", description = "Quản lý đặt trước sách")
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "Đặt trước sách", description = "Sinh viên tạo yêu cầu đặt trước sách")
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<ReservationResponse> create(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ReservationRequest request) {

        return ApiResponse.success(
                reservationService.create(userDetails.getUserId(), request)
        );
    }

    @Operation(summary = "Danh sách đặt trước của tôi", description = "Sinh viên xem danh sách đặt trước của chính mình")
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

    @Operation(summary = "Tìm kiếm đặt trước", description = "ADMIN, LIBRARIAN tìm kiếm danh sách đặt trước")
    @GetMapping("/search")
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ApiResponse<PageResponse<ReservationResponse>> search(@ModelAttribute ReservationSearchRequest request) {

        return ApiResponse.success(
                reservationService.search(request)
        );
    }

    @Operation(summary = "Danh sách đặt trước theo sinh viên", description = "ADMIN, LIBRARIAN xem danh sách đặt trước của một sinh viên")
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ApiResponse<PageResponse<ReservationResponse>> getByStudent(@PathVariable UUID studentId,
            @ModelAttribute ReservationSearchRequest request) {

        request.setStudentId(studentId);

        return ApiResponse.success(
                reservationService.search(request)
        );
    }

    @Operation(summary = "Hủy đặt trước", description = "Sinh viên hoặc thủ thư hủy yêu cầu đặt trước")
    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('STUDENT') or hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ApiResponse<ReservationResponse> cancel(@PathVariable UUID id) {

        return ApiResponse.success(
                reservationService.cancel(id)
        );
    }

    @Operation(summary = "Xác nhận đặt trước", description = "Thủ thư xác nhận yêu cầu đặt trước")
    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ApiResponse<ReservationResponse> confirm(@PathVariable UUID id) {

        return ApiResponse.success(
                reservationService.confirm(id)
        );
    }
}