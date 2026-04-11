package com.example.library_be.entity.enums;

public enum BorrowItemStatus {
    BORROWING,   // Đang mượn
    RETURNED,    // Đã trả (dù có thể kèm Fine nếu trả muộn)
    LOST,        // Mất sách
    DAMAGED      // Làm hỏng sách
}
