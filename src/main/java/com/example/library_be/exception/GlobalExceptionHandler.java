package com.example.library_be.exception;

import com.example.library_be.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.naming.AuthenticationException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({AccessDeniedException.class, AuthenticationException.class})
    public void handleSecurityException(Exception exception) throws Exception {
        throw exception; // trả lại cho Spring Security xử lý
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handlingException(Exception exception){
        exception.printStackTrace();
        ApiResponse<?> response = new ApiResponse<>();

        response.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        response.setMessage(exception.getMessage());

        return ResponseEntity
                .status(ErrorCode.UNCATEGORIZED_EXCEPTION.getHttpStatusCode())
                .body(response);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<?>> handlingAppException(AppException exception){
        ErrorCode errorCode = exception.getErrorCode();

        ApiResponse<?> response = new ApiResponse<>();

        response.setCode(errorCode.getCode());
        response.setMessage(exception.getMessage());

        return ResponseEntity
                .status(errorCode.getHttpStatusCode())
                .body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException exception) {

        String key = exception.getFieldError().getDefaultMessage();

        ErrorCode errorCode;

        try {
            errorCode = ErrorCode.valueOf(key);
        } catch (Exception e) {
            errorCode = ErrorCode.INVALID_KEY;
        }

        ApiResponse<?> response = new ApiResponse<>();

        response.setCode(errorCode.getCode());
        response.setMessage(errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getHttpStatusCode())
                .body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<?>> handleBadCredentials(BadCredentialsException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(1000, "Email hoặc mật khẩu không đúng"));
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse<?>> handleDisabled(DisabledException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(1000, "Tài khoản đã bị vô hiệu hóa"));
    }
}
