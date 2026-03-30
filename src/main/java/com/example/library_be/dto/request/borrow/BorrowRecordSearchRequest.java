package com.example.library_be.dto.request.borrow;

import com.example.library_be.entity.enums.BorrowStatus;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.util.UUID;

@Data
public class BorrowRecordSearchRequest {

    private UUID studentId; // dùng cho API theo sinh viên

    private BorrowStatus status; // dùng cho lọc trạng thái

    private String keyword;
    // search theo: fullName hoặc studentCode

    @Min(0)
    private Integer page = 0;

    @Min(1)
    private Integer size = 10;
}
