package com.likelion.demoday.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // =================================================================
    // 1. 공통 에러 (HTTP Status 그대로 사용)
    // =================================================================
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 400, "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, 401, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, 403, "접근 권한이 없습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, 404, "리소스를 찾을 수 없습니다."),
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "서버 내부 오류가 발생했습니다."),

    // =================================================================
    // 2. 도메인별 에러 (커스텀 코드 사용)
    // =================================================================

    // --- 사용자 관련 (1000번대) ---
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 1001, "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, 1002, "이미 존재하는 사용자입니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, 1003, "이미 존재하는 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, 1004, "이미 존재하는 닉네임입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, 1005, "이메일 또는 비밀번호가 올바르지 않습니다."),

    // --- 펀딩 관련 (2000번대) ---
    FUNDING_NOT_FOUND(HttpStatus.NOT_FOUND, 2001, "펀딩을 찾을 수 없습니다."),
    FUNDING_ALREADY_ENDED(HttpStatus.BAD_REQUEST, 2002, "이미 종료된 펀딩입니다."),
    FUNDING_NOT_OWNER(HttpStatus.FORBIDDEN, 2003, "펀딩 소유자가 아닙니다."),
    INVALID_TARGET_AMOUNT(HttpStatus.BAD_REQUEST, 2004, "목표 금액은 최소 10,000원 이상이어야 합니다."),
    INVALID_DEADLINE(HttpStatus.BAD_REQUEST, 2005, "마감일은 현재 시간 이후여야 합니다."),
    FUNDING_EXPIRED(HttpStatus.BAD_REQUEST, 2006, "마감일이 지난 펀딩입니다."),
    FORBIDDEN_USER(HttpStatus.FORBIDDEN, 2007, "해당 펀딩의 주인이 아닙니다."),
    ALREADY_PAID_OUT(HttpStatus.BAD_REQUEST, 2008, "이미 정산이 완료된 펀딩입니다."),

    // --- 참여(Contribution) 관련 (3000번대) ---
    CONTRIBUTION_NOT_FOUND(HttpStatus.NOT_FOUND, 3001, "참여 내역을 찾을 수 없습니다."),
    INVALID_CONTRIBUTION_AMOUNT(HttpStatus.BAD_REQUEST, 3002, "참여 금액은 최소 1,000원 이상이어야 합니다."),

    // --- 결제(Payment) 관련 (4000번대) ---
    PAYMENT_FAILED(HttpStatus.BAD_REQUEST, 4001, "결제 처리에 실패했습니다."),
    PAYMENT_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, 4002, "이미 처리된 결제입니다."),
    INVALID_WEBHOOK_SIGNATURE(HttpStatus.UNAUTHORIZED, 4003, "웹훅 서명이 유효하지 않습니다."),
    DUPLICATE_WEBHOOK(HttpStatus.BAD_REQUEST, 4004, "중복된 웹훅 요청입니다."),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, 4005, "결제 금액이 일치하지 않습니다."),
    PAYMENT_NOT_PAID(HttpStatus.BAD_REQUEST, 4006, "결제가 완료되지 않았습니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, 4007, "결제 정보를 찾을 수 없습니다."),
    PAYMENT_AMOUNT_EXCEEDS_REMAINING(HttpStatus.BAD_REQUEST, 4008, "결제 금액이 남은 금액을 초과합니다."),

    // --- 기타/시스템 (5000번대) ---
    IMAGE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 5001, "이미지 업로드에 실패했습니다.");

    private final HttpStatus httpStatus; // 실제 HTTP 상태 코드 (예: 404, 400)
    private final int code;              // 우리끼리 약속한 커스텀 코드 (예: 1001)
    private final String message;        // 메시지
}