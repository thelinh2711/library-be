package com.example.library_be.scheduler;

import com.example.library_be.repository.BorrowItemRepository;
import com.example.library_be.repository.BorrowRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class BorrowScheduler {

    private final BorrowRecordRepository borrowRecordRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void autoMarkOverdue() {
        borrowRecordRepository.markOverdue(LocalDate.now());
    }
}