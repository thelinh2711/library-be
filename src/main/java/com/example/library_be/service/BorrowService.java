package com.example.library_be.service;

import com.example.library_be.dto.request.borrow.BorrowRecordSearchRequest;
import com.example.library_be.dto.request.borrow.BorrowRequest;
import com.example.library_be.dto.request.borrow.ReturnRequest;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.borrow.BorrowRecordResponse;
import java.util.UUID;

public interface BorrowService {
    BorrowRecordResponse createBorrow(BorrowRequest request);
    BorrowRecordResponse processReturn(UUID recordId, ReturnRequest request);
    BorrowRecordResponse getById(UUID recordId);
    PageResponse<BorrowRecordResponse> search(BorrowRecordSearchRequest request);
}
