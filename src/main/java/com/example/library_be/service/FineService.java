package com.example.library_be.service;

import com.example.library_be.dto.request.fine.FineSearchRequest;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.borrow.BorrowRecordResponse;
import java.util.UUID;

public interface FineService {
    BorrowRecordResponse.FineResponse pay(UUID fineId);

    PageResponse<BorrowRecordResponse.FineResponse> getByStudent(UUID studentId, int page, int size);

    PageResponse<BorrowRecordResponse.FineResponse> search(FineSearchRequest request);
}
