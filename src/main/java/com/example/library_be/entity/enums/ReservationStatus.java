package com.example.library_be.entity.enums;

public enum ReservationStatus {
    PENDING,     // Vừa đặt, chờ xác nhận
    CONFIRMED,   // Thủ thư xác nhận, sách được giữ
    CANCELLED,   // Sinh viên huỷ
    EXPIRED,// Hết hạn giữ chỗ mà không đến nhận
    COMPLETED
}
