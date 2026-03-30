package com.example.library_be.service.impl;

import com.example.library_be.dto.request.borrow.BorrowRecordSearchRequest;
import com.example.library_be.dto.request.borrow.BorrowRequest;
import com.example.library_be.dto.request.borrow.ReturnRequest;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.borrow.BorrowRecordResponse;
import com.example.library_be.entity.*;
import com.example.library_be.entity.enums.*;
import com.example.library_be.exception.AppException;
import com.example.library_be.exception.ErrorCode;
import com.example.library_be.mapper.BorrowMapper;
import com.example.library_be.repository.*;
import com.example.library_be.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final BorrowItemRepository borrowItemRepository;
    private final BookRepository bookRepository;
    private final StudentRepository studentRepository;
    private final BookReservationRepository reservationRepository;
    private final FineRepository fineRepository;
    private final FinePolicyRepository finePolicyRepository;
    private final BorrowMapper borrowMapper;

    // ── Thủ thư tạo phiếu mượn ──────────────────────────────────────────────
    @Transactional
    @Override
    public BorrowRecordResponse createBorrow(BorrowRequest request) {

        var student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        if (fineRepository.hasUnpaidFine(student.getId())) {
            throw new AppException(ErrorCode.STUDENT_HAS_UNPAID_FINE);
        }

        // Validate reservation nếu có
        BookReservation reservation = null;
        if (request.getReservationId() != null) {
            reservation = reservationRepository.findById(request.getReservationId())
                    .orElseThrow(() -> new AppException(ErrorCode.RESERVATION_NOT_FOUND));

            if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
                throw new AppException(ErrorCode.RESERVATION_NOT_CONFIRMED);
            }
            if (reservation.getExpiredAt().isBefore(LocalDateTime.now())) {
                throw new AppException(ErrorCode.RESERVATION_EXPIRED);
            }
        }

        var record = new BorrowRecord();
        record.setStudent(student);
        record.setReservation(reservation);
        record.setStaffNote(request.getStaffNote());
        record = borrowRecordRepository.save(record);

        for (var itemReq : request.getItems()) {
            var book = bookRepository.findById(itemReq.getBookId())
                    .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

            if (book.getAvailableQuantity() <= 0) {
                throw new AppException(ErrorCode.BOOK_NOT_AVAILABLE);
            }

            book.setAvailableQuantity(book.getAvailableQuantity() - 1);
            bookRepository.save(book);

            var item = new BorrowItem();
            item.setBorrowRecord(record);
            item.setBook(book);
            item.setDueDate(itemReq.getDueDate());
            // status = BORROWING qua @PrePersist
            borrowItemRepository.save(item);
        }

        // Đóng reservation sau khi đã tạo phiếu mượn
        if (reservation != null) {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservationRepository.save(reservation);
        }

        // Load lại để có đủ items cho mapper
        record = borrowRecordRepository.findById(record.getId()).orElseThrow();
        return borrowMapper.toResponse(record);
    }

    // ── Thủ thư xử lý trả sách ──────────────────────────────────────────────
    @Transactional
    @Override
    public BorrowRecordResponse processReturn(UUID recordId, ReturnRequest request) {

        var record = borrowRecordRepository.findById(recordId)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));

        LocalDate today = LocalDate.now();

        for (var itemReq : request.getItems()) {

            var item = borrowItemRepository.findById(itemReq.getBorrowItemId())
                    .orElseThrow(() -> new AppException(ErrorCode.BORROW_ITEM_NOT_FOUND));

            if (item.getStatus() != BorrowItemStatus.BORROWING) {
                throw new AppException(ErrorCode.BORROW_ITEM_ALREADY_RETURNED);
            }

            if ((itemReq.getStatus() == BorrowItemStatus.DAMAGED
                    || itemReq.getStatus() == BorrowItemStatus.LOST)
                    && itemReq.getDamageLevel() == null) {

                throw new AppException(ErrorCode.DAMAGE_LEVEL_REQUIRED);
            }

            item.setReturnDate(today);
            item.setStatus(itemReq.getStatus());

            // Phạt trả muộn — tự động
            if (today.isAfter(item.getDueDate())) {
                createLateFine(item, today, itemReq.getNote());
            }

            // Phạt hỏng/mất — thủ thư nhập
            if (itemReq.getStatus() == BorrowItemStatus.DAMAGED
                    || itemReq.getStatus() == BorrowItemStatus.LOST) {
                FineType type = itemReq.getStatus() == BorrowItemStatus.LOST
                        ? FineType.LOST : FineType.DAMAGED;
                createDamageFine(item, type, itemReq.getDamageLevel(), itemReq.getNote());
            }

            // Hoàn lại số lượng (chỉ khi không phải LOST)
            if (itemReq.getStatus() != BorrowItemStatus.LOST) {
                var book = item.getBook();
                book.setAvailableQuantity(book.getAvailableQuantity() + 1);
                bookRepository.save(book);
            }

            borrowItemRepository.save(item);
        }

        // Cập nhật status BorrowRecord nếu tất cả items đã xong
        boolean allDone = record.getItems().stream()
                .allMatch(i -> i.getStatus() != BorrowItemStatus.BORROWING);
        if (allDone) record.setStatus(BorrowStatus.COMPLETED);

        return borrowMapper.toResponse(borrowRecordRepository.save(record));
    }

    @Override
    public BorrowRecordResponse getById(UUID recordId) {
        return borrowMapper.toResponse(
                borrowRecordRepository.findById(recordId)
                        .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND)));
    }

    @Override
    public PageResponse<BorrowRecordResponse> search(BorrowRecordSearchRequest request) {

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        Page<BorrowRecord> pageData;

        UUID studentId = request.getStudentId();
        BorrowStatus status = request.getStatus();
        String keyword = request.getKeyword();

        // CASE 1: search theo studentId
        if (studentId != null) {
            pageData = borrowRecordRepository
                    .findByStudentIdOrderByBorrowedAtDesc(studentId, pageable);

            // CASE 2: search theo status + keyword
        } else if (status != null) {
            if (keyword != null && !keyword.isBlank()) {
                pageData = borrowRecordRepository.searchByStatusAndKeyword(status, keyword, pageable);
            } else {
                pageData = borrowRecordRepository.findByStatusOrderByBorrowedAtDesc(status, pageable);
            }

            // CASE 3: không truyền gì → lấy tất cả
        } else {
            pageData = borrowRecordRepository.findAllByOrderByBorrowedAtDesc(pageable);
        }

        return PageResponse.from(pageData.map(borrowMapper::toResponse));
    }

    // ── Fine helpers ─────────────────────────────────────────────────────────

    private void createLateFine(BorrowItem item, LocalDate returnDate, String note) {
        long overdueDays = ChronoUnit.DAYS.between(item.getDueDate(), returnDate);
        List<FinePolicy> brackets = finePolicyRepository
                .findByTypeAndIsActiveTrueOrderByDayFromAsc(FineType.LATE);

        BigDecimal amount = BigDecimal.ZERO;
        long remaining = overdueDays;
        FinePolicy appliedPolicy = brackets.isEmpty() ? null : brackets.get(0);

        for (FinePolicy bracket : brackets) {
            if (remaining <= 0) break;
            appliedPolicy = bracket;
            long bracketEnd = bracket.getDayTo() != null ? bracket.getDayTo() : Long.MAX_VALUE;
            long daysInBracket = Math.min(remaining, bracketEnd - (bracket.getDayFrom() - 1));
            if (daysInBracket <= 0) continue;
            amount = amount.add(bracket.getAmountPerDay().multiply(BigDecimal.valueOf(daysInBracket)));
            remaining -= daysInBracket;
        }

        Fine fine = new Fine();
        fine.setBorrowItem(item);
        fine.setFinePolicy(appliedPolicy);
        fine.setType(FineType.LATE);
        fine.setAmount(amount);
        fine.setNote("Trả muộn " + overdueDays + " ngày" + (note != null ? " — " + note : ""));
        fineRepository.save(fine);
    }

    private void createDamageFine(BorrowItem item, FineType type,
                                  DamageLevel damageLevel, String note) {
        var policy = finePolicyRepository
                .findByTypeAndDamageLevelAndIsActiveTrue(type, damageLevel)
                .orElseThrow(() -> new AppException(ErrorCode.FINE_POLICY_NOT_FOUND));

        Fine fine = new Fine();
        fine.setBorrowItem(item);
        fine.setFinePolicy(policy);
        fine.setType(type);
        fine.setAmount(item.getBook().getPrice().multiply(policy.getMultiplier()));
        fine.setNote(damageLevel + (note != null ? " — " + note : ""));
        fineRepository.save(fine);
    }
}