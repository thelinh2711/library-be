package com.example.library_be.service.impl;

import com.example.library_be.dto.request.fine.FineSearchRequest;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.borrow.BorrowRecordResponse;
import com.example.library_be.entity.enums.PaymentStatus;
import com.example.library_be.exception.AppException;
import com.example.library_be.exception.ErrorCode;
import com.example.library_be.mapper.BorrowMapper;
import com.example.library_be.repository.FineRepository;
import com.example.library_be.service.FineService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FineServiceImpl implements FineService {

    private final FineRepository fineRepository;
    private final BorrowMapper borrowMapper;

    @Transactional
    @Override
    public BorrowRecordResponse.FineResponse pay(UUID fineId) {
        var fine = fineRepository.findById(fineId)
                .orElseThrow(() -> new AppException(ErrorCode.FINE_NOT_FOUND));

        if (fine.getPaymentStatus() == PaymentStatus.PAID) {
            throw new AppException(ErrorCode.FINE_ALREADY_PAID);
        }

        fine.setPaymentStatus(PaymentStatus.PAID);
        fine.setPaidAt(LocalDateTime.now());

        return borrowMapper.toFineResponse(fineRepository.save(fine));
    }
    @Override
    public PageResponse<BorrowRecordResponse.FineResponse> getByStudent(UUID studentId, int page, int size) {

        var pageData = fineRepository.findByStudentId(
                studentId,
                PageRequest.of(page, size)
        );

        return PageResponse.from(
                pageData.map(borrowMapper::toFineResponse)
        );
    }

    @Override
    public PageResponse<BorrowRecordResponse.FineResponse> search(FineSearchRequest request) {

        String keyword = request.getKeyword();
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword;

        var pageData = fineRepository.searchFines(
                request.getStatus(),
                kw,
                PageRequest.of(request.getPage(), request.getSize())
        );

        return PageResponse.from(
                pageData.map(borrowMapper::toFineResponse)
        );
    }
}