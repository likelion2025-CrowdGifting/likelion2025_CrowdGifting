package com.likelion.demoday.global.response;

import com.likelion.demoday.global.exception.ErrorCode;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class ErrorResponse {

    private final boolean success = false;
    private final int code;
    private final String message;
    private final LocalDateTime timestamp;

    private ErrorResponse(int code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    // 1. ErrorCode만 받아서 생성 (기본 메시지 사용)
    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
    }

    // 2. ErrorCode와 커스텀 메시지를 받아서 생성 (유효성 검사 에러용)
    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return new ErrorResponse(errorCode.getCode(), message);
    }
}