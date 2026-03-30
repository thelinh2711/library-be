package com.example.library_be.controller;

import com.example.library_be.dto.request.borrow.BorrowRecordSearchRequest;
import com.example.library_be.dto.request.borrow.BorrowRequest;
import com.example.library_be.dto.request.borrow.ReturnRequest;
import com.example.library_be.dto.response.ApiResponse;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.borrow.BorrowRecordResponse;
import com.example.library_be.entity.User;
import com.example.library_be.entity.enums.BorrowStatus;
import com.example.library_be.service.BorrowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/borrows")
@RequiredArgsConstructor
public class BorrowController {

    private final BorrowService borrowService;

    // Thủ thư tạo phiếu mượn
    @PostMapping
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ApiResponse<BorrowRecordResponse> create(
            @Valid @RequestBody BorrowRequest request) {
        return ApiResponse.success(borrowService.createBorrow(request));
    }

    // Thủ thư xử lý trả sách
    @PostMapping("/{id}/return")
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ApiResponse<BorrowRecordResponse> processReturn(
            @PathVariable UUID id,
            @Valid @RequestBody ReturnRequest request) {
        return ApiResponse.success(borrowService.processReturn(id, request));
    }

    // Xem chi tiết 1 phiếu mượn
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN') or hasRole('STUDENT')")
    public ApiResponse<BorrowRecordResponse> getById(@PathVariable UUID id) {
        return ApiResponse.success(borrowService.getById(id));
    }

    // Sinh viên xem lịch sử mượn của mình
    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public ApiResponse<PageResponse<BorrowRecordResponse>> getMy(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        BorrowRecordSearchRequest request = new BorrowRecordSearchRequest();
        request.setStudentId(user.getId());
        request.setPage(page);
        request.setSize(size);

        return ApiResponse.success(borrowService.search(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('ADMIN')")
    public ApiResponse<PageResponse<BorrowRecordResponse>> search(
            BorrowRecordSearchRequest request) {
        return ApiResponse.success(borrowService.search(request));
    }
}