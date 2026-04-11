package com.example.library_be.runner;

import com.example.library_be.repository.BorrowItemRepository;
import com.example.library_be.repository.BorrowRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class StartupRunner implements CommandLineRunner {

    private final BorrowRecordRepository borrowRecordRepository;

    @Override
    @Transactional
    public void run(String... args) {
        borrowRecordRepository.markOverdue(LocalDate.now());
    }
}
