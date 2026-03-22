package com.example.library_be.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
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
    UNAUTHENTICATED(1009, "Chưa xác thực", HttpStatus.UNAUTHORIZED),
    CATEGORY_NOT_FOUND(1010, "Category not found", HttpStatus.NOT_FOUND),
    CATEGORY_ALREADY_EXISTS(1011, "Category already exists", HttpStatus.BAD_REQUEST),
    AUTHOR_NOT_FOUND(1012, "Author not found", HttpStatus.NOT_FOUND),
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