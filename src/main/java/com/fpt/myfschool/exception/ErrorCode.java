package com.fpt.myfschool.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // SYSTEM ERRORS
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi hệ thống không xác định", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(8888, "Invalid message key", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1020, "Vui lòng đăng nhập", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1004, "Bạn không có quyền truy cập", HttpStatus.FORBIDDEN),

    // USER ERRORS
    USER_NOT_FOUND(1001, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    USER_EXISTED(1002, "Tài khoản đã tồn tại", HttpStatus.CONFLICT),
    EMAIL_EXISTED(1003, "Email đã được sử dụng", HttpStatus.CONFLICT),
    INVALID_CREDENTIALS(1009, "Sai tài khoản hoặc mật khẩu", HttpStatus.UNAUTHORIZED),

    // GENERAL
    RESOURCE_NOT_FOUND(1044, "Không tìm thấy dữ liệu", HttpStatus.NOT_FOUND);

    private final int code;
    private final String message;
    private final HttpStatus status;
}