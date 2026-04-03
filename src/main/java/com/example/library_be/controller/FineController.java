package com.example.library_be.controller;

import com.example.library_be.dto.request.fine.FineSearchRequest;
import com.example.library_be.dto.response.ApiResponse;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.borrow.BorrowRecordResponse;
import com.example.library_be.entity.enums.PaymentStatus;
import com.example.library_be.security.CustomUserDetails;
import com.example.library_be.service.FineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/fines")
@RequiredArgsConstructor
public class FineController {

    private final FineService fineService;

    // Thủ thư xác nhận thu tiền phạt
    @PatchMapping("/{id}/pay")
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ApiResponse<BorrowRecordResponse.FineResponse> pay(@PathVariable UUID id) {
        return ApiResponse.success(fineService.pay(id));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<PageResponse<BorrowRecordResponse.FineResponse>> getMy(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        UUID studentId = userDetails.getUserId();

        return ApiResponse.success(
                fineService.getByStudent(studentId, page, size)
        );
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ApiResponse<PageResponse<BorrowRecordResponse.FineResponse>> getByStudent(
            @PathVariable UUID studentId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(fineService.getByStudent(studentId, page, size));
    }

    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ApiResponse<PageResponse<BorrowRecordResponse.FineResponse>> search(
            FineSearchRequest request) {
        return ApiResponse.success(fineService.search(request));
    }
}
