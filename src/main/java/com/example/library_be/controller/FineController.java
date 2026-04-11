package com.example.library_be.controller;

import com.example.library_be.dto.request.fine.FineSearchRequest;
import com.example.library_be.dto.response.ApiResponse;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.borrow.BorrowRecordResponse;
import com.example.library_be.entity.enums.PaymentStatus;
import com.example.library_be.security.CustomUserDetails;
import com.example.library_be.service.FineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Fine", description = "Quản lý tiền phạt")
public class FineController {

    private final FineService fineService;

    @Operation(summary = "Thanh toán tiền phạt", description = "Thủ thư xác nhận đã thu tiền phạt")
    @PatchMapping("/{id}/pay")
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ApiResponse<BorrowRecordResponse.FineResponse> pay(@PathVariable UUID id) {
        return ApiResponse.success(fineService.pay(id));
    }

    @Operation(summary = "Danh sách tiền phạt của tôi", description = "Sinh viên xem các khoản tiền phạt của mình")
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

    @Operation(summary = "Danh sách tiền phạt theo sinh viên", description = "Thủ thư xem tiền phạt của một sinh viên")
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ApiResponse<PageResponse<BorrowRecordResponse.FineResponse>> getByStudent(
            @PathVariable UUID studentId,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size) {
        return ApiResponse.success(fineService.getByStudent(studentId, page, size));
    }

    @Operation(summary = "Tìm kiếm tiền phạt", description = "Phân trang, tìm kiếm tiền phạt")
    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ApiResponse<PageResponse<BorrowRecordResponse.FineResponse>> search(
            FineSearchRequest request) {
        return ApiResponse.success(fineService.search(request));
    }
}
