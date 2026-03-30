package com.example.library_be.dto.request.borrow;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class BorrowRequest {

    @NotNull(message = "Mã sinh viên không được để trống")
    private UUID studentId;

    // null nếu mượn trực tiếp không qua đặt trước
    private UUID reservationId;

    private String staffNote;

    @NotEmpty(message = "Phải có ít nhất 1 quyển sách")
    @Size(max = 5, message = "Mỗi lần chỉ được mượn tối đa 5 quyển")
    @Valid
    private List<BorrowItemRequest> items;

    @Data
    public static class BorrowItemRequest {
        @NotNull(message = "Mã sách không được để trống")
        private UUID bookId;

        @NotNull(message = "Hạn trả không được để trống")
        private LocalDate dueDate;
    }
}
