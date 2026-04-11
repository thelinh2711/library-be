package com.example.library_be.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST(1000, "Yêu cầu không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_KEY(1001, "Invalid message key", HttpStatus.BAD_REQUEST),

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