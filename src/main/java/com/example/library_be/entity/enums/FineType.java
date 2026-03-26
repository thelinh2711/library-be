package com.example.library_be.entity.enums;

public enum FineType {
    LATE,       // Trả muộn — tự động tạo khi returnDate > dueDate
    DAMAGED,    // Làm hỏng sách — thủ thư tạo thủ công
    LOST        // Làm mất sách — thủ thư tạo thủ công
}
