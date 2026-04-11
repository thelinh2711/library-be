package com.example.library_be.controller;

import com.example.library_be.dto.request.borrow.BorrowRecordSearchRequest;
import com.example.library_be.dto.request.borrow.BorrowRequest;
import com.example.library_be.dto.request.borrow.ReturnRequest;
import com.example.library_be.dto.response.ApiResponse;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.borrow.BorrowRecordResponse;
import com.example.library_be.entity.User;
import com.example.library_be.entity.enums.BorrowStatus;
import com.example.library_be.security.CustomUserDetails;
import com.example.library_be.service.BorrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/borrows")
@RequiredArgsConstructor
@Tag(name = "Borrow", description = "Quản lý mượn trả sách")
public class BorrowController {

    private final BorrowService borrowService;

    @Operation(summary = "Tạo phiếu mượn", description = "Thủ thư tạo phiếu mượn sách")
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<BorrowRecordResponse> create(
            @Valid @RequestBody BorrowRequest request) {
        return ApiResponse.success(borrowService.createBorrow(request));
    }

    @Operation(summary = "Trả sách", description = "Xử lý trả sách cho phiếu mượn")
    @PostMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<BorrowRecordResponse> processReturn(
            @PathVariable UUID id,
            @Valid @RequestBody ReturnRequest request) {
        return ApiResponse.success(borrowService.processReturn(id, request));
    }

    @Operation(summary = "Chi tiết phiếu mượn", description = "Lấy thông tin chi tiết phiếu mượn theo ID")
    @GetMapping("/{id}")
    public ApiResponse<BorrowRecordResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(borrowService.getById(id));
    }

    @Operation(summary = "Lịch sử mượn của tôi", description = "Sinh viên xem lịch sử mượn sách của chính mình")
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<PageResponse<BorrowRecordResponse>> getMy(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        BorrowRecordSearchRequest request = new BorrowRecordSearchRequest();
        request.setStudentId(userDetails.getUserId());
        request.setPage(page);
        request.setSize(size);

        return ApiResponse.success(borrowService.search(request));
    }

    @Operation(summary = "Tìm kiếm phiếu mượn", description = "Phân trang, tìm kiếm phiếu mượn")
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'LIBRARIAN')")
    public ApiResponse<PageResponse<BorrowRecordResponse>> search(BorrowRecordSearchRequest request) {
        return ApiResponse.success(borrowService.search(request));
    }
}