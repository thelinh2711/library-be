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
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

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

    // ── Helper: load full graph vào session cache
    private BorrowRecord fetchFullGraph(UUID id) {
        borrowRecordRepository.findByIdWithFines(id); // merge fines vào cache
        return borrowRecordRepository.findByIdWithItems(id)
                .orElseThrow(() -> new AppException(ErrorCode.BORROW_RECORD_NOT_FOUND));
    }

    // Thủ thư tạo phiếu mượn
    @Transactional
    @Override
    public BorrowRecordResponse createBorrow(BorrowRequest request) {

        var student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        if (fineRepository.hasUnpaidFine(student.getId())) {
            throw new AppException(ErrorCode.STUDENT_HAS_UNPAID_FINE);
        }

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

        // BATCH LOAD BOOK
        List<UUID> bookIds = request.getItems().stream()
                .map(item -> item.getBookId())
                .distinct()
                .toList();

        Map<UUID, Book> bookMap = bookRepository.findAllById(bookIds)
                .stream()
                .collect(Collectors.toMap(Book::getId, b -> b));

        // validate đủ book
        if (bookMap.size() != bookIds.size()) {
            throw new AppException(ErrorCode.BOOK_NOT_FOUND);
        }

        for (var itemReq : request.getItems()) {
            var book = bookMap.get(itemReq.getBookId());

            if (book.getAvailableQuantity() <= 0) {
                throw new AppException(ErrorCode.BOOK_NOT_AVAILABLE);
            }

            book.setAvailableQuantity(book.getAvailableQuantity() - 1);

            var item = new BorrowItem();
            item.setBorrowRecord(record);
            item.setBook(book);
            item.setDueDate(itemReq.getDueDate());
            borrowItemRepository.save(item);
        }

        if (reservation != null) {
            reservation.setStatus(ReservationStatus.CONFIRMED);
        }

        return borrowMapper.toResponse(fetchFullGraph(record.getId()));
    }

    // Thủ thư xử lý trả sách
    @Transactional
    @Override
    public BorrowRecordResponse processReturn(UUID recordId, ReturnRequest request) {

        var record = fetchFullGraph(recordId);
        LocalDate today = LocalDate.now();

        // BATCH LOAD ITEMS
        List<UUID> itemIds = request.getItems().stream()
                .map(ReturnRequest.ReturnItemRequest::getBorrowItemId)
                .toList();

        Map<UUID, BorrowItem> itemMap = borrowItemRepository.findAllById(itemIds)
                .stream()
                .collect(Collectors.toMap(BorrowItem::getId, i -> i));

        if (itemMap.size() != itemIds.size()) {
            throw new AppException(ErrorCode.BORROW_ITEM_NOT_FOUND);
        }

        for (var itemReq : request.getItems()) {
            var item = itemMap.get(itemReq.getBorrowItemId());
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

            if (today.isAfter(item.getDueDate())) {
                createLateFine(item, today, itemReq.getNote());
            }

            if (itemReq.getStatus() == BorrowItemStatus.DAMAGED || itemReq.getStatus() == BorrowItemStatus.LOST) {
                FineType type = itemReq.getStatus() == BorrowItemStatus.LOST ? FineType.LOST : FineType.DAMAGED;
                createDamageFine(item, type, itemReq.getDamageLevel(), itemReq.getNote());
            }

            if (itemReq.getStatus() != BorrowItemStatus.LOST) {
                var book = item.getBook();
                book.setAvailableQuantity(book.getAvailableQuantity() + 1);
            }
        }

        boolean allDone = record.getItems().stream()
                .allMatch(i -> i.getStatus() != BorrowItemStatus.BORROWING);
        if (allDone) record.setStatus(BorrowStatus.COMPLETED);

        return borrowMapper.toResponse(fetchFullGraph(recordId));
    }

    // getById
    @Override
    @Transactional(readOnly = true)
    public BorrowRecordResponse getById(UUID recordId) {
        return borrowMapper.toResponse(fetchFullGraph(recordId));
    }

    // search
    @Override
    @Transactional(readOnly = true)
    public PageResponse<BorrowRecordResponse> search(BorrowRecordSearchRequest request) {

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

        UUID studentId = request.getStudentId();
        BorrowStatus status = request.getStatus();
        String keyword = request.getKeyword();

        Page<UUID> idPage;
        if (studentId != null) {
            idPage = borrowRecordRepository.findIdsByStudentId(studentId, pageable);
        } else if (status != null) {
            if (keyword != null && !keyword.isBlank()) {
                idPage = borrowRecordRepository.findIdsByStatusAndKeyword(status, keyword, pageable);
            } else {
                idPage = borrowRecordRepository.findIdsByStatus(status, pageable);
            }
        } else {
            idPage = borrowRecordRepository.findAllIds(pageable);
        }

        List<UUID> ids = idPage.getContent();
        if (ids.isEmpty()) {
            return PageResponse.from(new org.springframework.data.domain.PageImpl<>(
                    List.of(), pageable, 0));
        }

        // 2 query, Hibernate merge vào session cache
        borrowRecordRepository.fetchWithFines(ids);
        List<BorrowRecord> records = borrowRecordRepository.fetchWithItems(ids);

        Map<UUID, BorrowRecord> map = records.stream()
                .collect(Collectors.toMap(BorrowRecord::getId, r -> r));

        List<BorrowRecordResponse> content = ids.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .map(borrowMapper::toResponse)
                .toList();

        return PageResponse.from(new org.springframework.data.domain.PageImpl<>(
                content, pageable, idPage.getTotalElements()));
    }


    // Fine helpers
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