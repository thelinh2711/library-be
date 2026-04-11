package com.example.library_be.entity.enums;

public enum BorrowStatus {
    BORROWING,   // Đang mượn (còn ít nhất 1 quyển chưa trả)
    COMPLETED,   // Tất cả quyển đã trả
    OVERDUE      // Có ít nhất 1 quyển quá hạn
}
