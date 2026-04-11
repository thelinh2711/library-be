package com.example.library_be.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

    UNAUTHENTICATED(2001, "Chưa xác thực", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(2002, "Không có quyền", HttpStatus.FORBIDDEN),
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST(1000, "Yêu cầu không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_KEY(1001, "Invalid message key", HttpStatus.BAD_REQUEST),
    USERNAME_ALREADY_EXISTS(1002, "Username đã tồn tại", HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS(1003, "Email đã tồn tại", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH(1004, "Mật khẩu không khớp", HttpStatus.BAD_REQUEST),
    PASSWORD_TOO_SHORT(1005, "Mật khẩu phải tối thiểu 8 ký tự", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1006, "User không tồn tại", HttpStatus.NOT_FOUND),
    INVALID_PASSWORD(1007, "Sai mật khẩu", HttpStatus.UNAUTHORIZED),
    INVALID_REFRESH_TOKEN(1008, "Refresh token không hợp lệ", HttpStatus.UNAUTHORIZED),
    //UNAUTHENTICATED(1009, "Chưa xác thực", HttpStatus.UNAUTHORIZED),
    CATEGORY_NOT_FOUND(1010, "Category not found", HttpStatus.NOT_FOUND),
    CATEGORY_ALREADY_EXISTS(1011, "Category already exists", HttpStatus.BAD_REQUEST),
    AUTHOR_NOT_FOUND(1012, "Author not found", HttpStatus.NOT_FOUND),
    BOOK_NOT_FOUND(1013, "Book not found", HttpStatus.NOT_FOUND),
    CATEGORY_IN_USE(1014, "Không thể xóa category vì đang có sách sử dụng", HttpStatus.BAD_REQUEST),
    AUTHOR_IN_USE(1015, "Cannot delete author because it is being used by books", HttpStatus.BAD_REQUEST),
    INVALID_QUANTITY(1016, "Available quantity must be less than or equal to total quantity", HttpStatus.BAD_REQUEST),
    STUDENT_NOT_FOUND(1017, "Không tìm thấy sinh viên", HttpStatus.NOT_FOUND),
    USER_EMAIL_EXIST(1018, "Email đã tồn tại", HttpStatus.BAD_REQUEST),
    STUDENT_CODE_EXIST(1019, "Mã sinh viên đã tồn tại", HttpStatus.BAD_REQUEST),
    RESERVATION_ALREADY_EXISTS(1020, "Bạn đã có đặt trước đang chờ xử lý cho cuốn sách này", HttpStatus.BAD_REQUEST),
    RESERVATION_NOT_FOUND(1021, "Không tìm thấy đặt trước", HttpStatus.NOT_FOUND),
    RESERVATION_NOT_PENDING(1022, "Đặt trước không ở trạng thái chờ xác nhận", HttpStatus.BAD_REQUEST),
    RESERVATION_NOT_CONFIRMED(1023, "Đặt trước chưa được xác nhận", HttpStatus.BAD_REQUEST),
    RESERVATION_EXPIRED(1024, "Đặt trước đã hết hạn giữ chỗ", HttpStatus.BAD_REQUEST),

    BORROW_RECORD_NOT_FOUND(1025, "Không tìm thấy phiếu mượn", HttpStatus.NOT_FOUND),
    BORROW_ITEM_NOT_FOUND(1026, "Không tìm thấy chi tiết mượn sách", HttpStatus.NOT_FOUND),
    BORROW_ITEM_ALREADY_RETURNED(1027, "Sách này đã được trả", HttpStatus.BAD_REQUEST),
    BOOK_NOT_AVAILABLE(1028, "Sách hiện không còn trong kho", HttpStatus.BAD_REQUEST),
    STUDENT_HAS_UNPAID_FINE(1029, "Sinh viên còn khoản phạt chưa thanh toán", HttpStatus.BAD_REQUEST),

    FINE_NOT_FOUND(1030, "Không tìm thấy phiếu phạt", HttpStatus.NOT_FOUND),
    FINE_ALREADY_PAID(1031, "Phiếu phạt đã được thanh toán", HttpStatus.BAD_REQUEST),
    FINE_POLICY_NOT_FOUND(1032, "Không tìm thấy chính sách phạt phù hợp", HttpStatus.NOT_FOUND),
    DAMAGE_LEVEL_REQUIRED(1033, "Phải chỉ định mức độ khi trạng thái là DAMAGED hoặc LOST", HttpStatus.BAD_REQUEST),
    NEW_PASSWORD_DUPLICATE(1034, "Mật khẩu mới phải khác mật khẩu cũ", HttpStatus.BAD_REQUEST),
    RESERVATION_INVALID_STATUS(1013, "Trạng thái đặt trước không hợp lệ", HttpStatus.BAD_REQUEST),

    // ── Common ─────────────────────────────────
    INVALID_STATUS(1014, "Giá trị trạng thái không hợp lệ", HttpStatus.BAD_REQUEST),
    CONFLICT(4090, "Dữ liệu đã bị thay đổi bởi người khác. Vui lòng tải lại.", HttpStatus.CONFLICT),
    ;

    ErrorCode(int code, String message, HttpStatus httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatus httpStatusCode;
}