package com.example.library_be.service.impl;

import com.example.library_be.dto.request.reservation.ReservationRequest;
import com.example.library_be.dto.request.reservation.ReservationSearchRequest;
import com.example.library_be.dto.response.PageResponse;
import com.example.library_be.dto.response.borrow.ReservationResponse;
import com.example.library_be.entity.BookReservation;
import com.example.library_be.entity.enums.ReservationStatus;
import com.example.library_be.exception.AppException;
import com.example.library_be.exception.ErrorCode;
import com.example.library_be.mapper.ReservationMapper;
import com.example.library_be.repository.BookRepository;
import com.example.library_be.repository.BookReservationRepository;
import com.example.library_be.repository.StudentRepository;
import com.example.library_be.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements ReservationService {

    private static final int HOLD_DAYS = 3;

    private final BookReservationRepository reservationRepository;
    private final BookRepository bookRepository;
    private final StudentRepository studentRepository;
    private final ReservationMapper reservationMapper;

    // CREATE
    @Transactional
    @Override
    public ReservationResponse create(UUID studentId, ReservationRequest request) {
        log.info("Create reservation studentId={}, bookId={}", studentId, request.getBookId());
        var student = studentRepository.findById(studentId)
                .orElseThrow(() -> new AppException(ErrorCode.STUDENT_NOT_FOUND));

        var book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        if (book.getAvailableQuantity() <= 0) {
            log.warn("Book not available bookId={}", book.getId());
            throw new AppException(ErrorCode.BOOK_NOT_AVAILABLE);
        }

        boolean alreadyReserved = reservationRepository.existsByStudentIdAndBookIdAndStatusIn(
                studentId,
                book.getId(),
                List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED)
        );

        if (alreadyReserved) {
            log.warn("Reservation already exists studentId={}, bookId={}", studentId, book.getId());
            throw new AppException(ErrorCode.RESERVATION_ALREADY_EXISTS);
        }

        var reservation = new BookReservation();
        reservation.setStudent(student);
        reservation.setBook(book);

        var saved = reservationRepository.save(reservation);
        log.info("Reservation created id={}", saved.getId());

        return reservationMapper.toResponse(saved);
    }

    // CONFIRM
    @Transactional
    @Override
    public ReservationResponse confirm(UUID reservationId) {

        var reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new AppException(ErrorCode.RESERVATION_NOT_FOUND));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new AppException(ErrorCode.RESERVATION_NOT_PENDING);
        }

        var book = bookRepository.findByIdForUpdate(reservation.getBook().getId())
                .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

        if (book.getAvailableQuantity() <= 0) {
            throw new AppException(ErrorCode.BOOK_NOT_AVAILABLE);
        }

        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        bookRepository.save(book);

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setExpiredAt(LocalDateTime.now().plusDays(HOLD_DAYS));

        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    // ─────────────────────────────────────────────
    // CANCEL
    // ─────────────────────────────────────────────
    @Transactional
    @Override
    public ReservationResponse cancel(UUID reservationId) {

        var reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new AppException(ErrorCode.RESERVATION_NOT_FOUND));

        if (reservation.getStatus() == ReservationStatus.CANCELLED
                || reservation.getStatus() == ReservationStatus.EXPIRED) {
            throw new AppException(ErrorCode.RESERVATION_INVALID_STATUS);
        }

        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            var book = bookRepository.findByIdForUpdate(reservation.getBook().getId())
                    .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

            book.setAvailableQuantity(book.getAvailableQuantity() + 1);
            bookRepository.save(book);
        }

        reservation.setStatus(ReservationStatus.CANCELLED);

        return reservationMapper.toResponse(reservationRepository.save(reservation));
    }

    // SEARCH
    @Override
    public PageResponse<ReservationResponse> search(ReservationSearchRequest request) {

        var pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<BookReservation> pageData;

        UUID studentId = request.getStudentId();
        String statusStr = request.getStatus();

        List<ReservationStatus> closedStatuses = List.of(
                ReservationStatus.CANCELLED,
                ReservationStatus.EXPIRED
        );

        if (studentId != null && statusStr != null) {

            if ("CLOSED".equalsIgnoreCase(statusStr)) {
                pageData = reservationRepository
                        .findByStudentIdAndStatusInOrderByReservedAtDesc(
                                studentId, closedStatuses, pageable);
            } else {
                pageData = reservationRepository
                        .findByStudentIdAndStatusOrderByReservedAtDesc(
                                studentId, parseStatus(statusStr), pageable);
            }

        } else if (studentId != null) {
            pageData = reservationRepository.findByStudentIdOrderByReservedAtDesc(studentId, pageable);
        } else if (statusStr != null) {
            if ("CLOSED".equalsIgnoreCase(statusStr)) {
                pageData = reservationRepository.findByStatusInOrderByReservedAtDesc(closedStatuses, pageable);
            } else {
                pageData = reservationRepository.findByStatusOrderByReservedAtDesc(parseStatus(statusStr), pageable);
            }
        } else {
            pageData = reservationRepository.findAll(pageable);
        }

        return PageResponse.from(pageData.map(reservationMapper::toResponse));
    }

    // ─────────────────────────────────────────────
    // AUTO EXPIRE
    // ─────────────────────────────────────────────
    @Transactional
    @Override
    public void expireReservations() {

        var list = reservationRepository.findAllByStatusAndExpiredAtBefore(
                ReservationStatus.CONFIRMED,
                LocalDateTime.now()
        );

        for (var r : list) {
            var book = bookRepository.findByIdForUpdate(r.getBook().getId())
                    .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

            book.setAvailableQuantity(book.getAvailableQuantity() + 1);
            bookRepository.save(book);

            r.setStatus(ReservationStatus.EXPIRED);
        }

        reservationRepository.saveAll(list);
    }

    private ReservationStatus parseStatus(String status) {
        try {
            return ReservationStatus.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new AppException(ErrorCode.INVALID_STATUS);
        }
    }
}