package com.example.library_be.dto.request.borrow;

import com.example.library_be.entity.enums.BorrowItemStatus;
import com.example.library_be.entity.enums.DamageLevel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class ReturnRequest {

    @NotEmpty(message = "Phải có ít nhất 1 item để trả")
    @Valid
    private List<ReturnItemRequest> items;

    @Data
    public static class ReturnItemRequest {
        @NotNull
        private UUID borrowItemId;

        // RETURNED | DAMAGED | LOST
        @NotNull
        private BorrowItemStatus status;

        // Bắt buộc khi status = DAMAGED | LOST
        // Khớp với FinePolicy.damageLevel: "Hỏng nhẹ" | "Hỏng nặng" | "Mất sách"
        private DamageLevel damageLevel;

        private String note;
    }
}
