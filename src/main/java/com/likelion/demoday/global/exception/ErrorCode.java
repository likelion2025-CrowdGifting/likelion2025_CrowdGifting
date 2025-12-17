package com.likelion.demoday.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    
    // 인증/인가 (400-499)
    BAD_REQUEST(400, "잘못된 요청입니다."),
    UNAUTHORIZED(401, "인증이 필요합니다."),
    FORBIDDEN(403, "접근 권한이 없습니다."),
    NOT_FOUND(404, "리소스를 찾을 수 없습니다."),
    
    // 사용자 관련 (1000-1999)
    USER_NOT_FOUND(1001, "사용자를 찾을 수 없습니다."),
    USER_ALREADY_EXISTS(1002, "이미 존재하는 사용자입니다."),
    EMAIL_ALREADY_EXISTS(1003, "이미 존재하는 이메일입니다."),
    NICKNAME_ALREADY_EXISTS(1004, "이미 존재하는 닉네임입니다."),
    INVALID_CREDENTIALS(1005, "이메일 또는 비밀번호가 올바르지 않습니다."),
    
    // 펀딩 관련 (2000-2999)
    FUNDING_NOT_FOUND(2001, "펀딩을 찾을 수 없습니다."),
    FUNDING_ALREADY_ENDED(2002, "이미 종료된 펀딩입니다."),
    FUNDING_NOT_OWNER(2003, "펀딩 소유자가 아닙니다."),
    INVALID_TARGET_AMOUNT(2004, "목표 금액은 최소 10,000원 이상이어야 합니다."),
    INVALID_DEADLINE(2005, "마감일은 현재 시간 이후여야 합니다."),
    
    // 참여 관련 (3000-3999)
    CONTRIBUTION_NOT_FOUND(3001, "참여 내역을 찾을 수 없습니다."),
    INVALID_CONTRIBUTION_AMOUNT(3002, "참여 금액은 최소 1,000원 이상이어야 합니다."),
    FUNDING_EXPIRED(3003, "마감일이 지난 펀딩입니다."),
    
    // 결제 관련 (4000-4999)
    PAYMENT_FAILED(4001, "결제 처리에 실패했습니다."),
    PAYMENT_ALREADY_PROCESSED(4002, "이미 처리된 결제입니다."),
    INVALID_WEBHOOK_SIGNATURE(4003, "웹훅 서명이 유효하지 않습니다."),
    DUPLICATE_WEBHOOK(4004, "중복된 웹훅 요청입니다."),
    
    // 서버 에러 (5000-5999)
    INTERNAL_SERVER_ERROR(5000, "서버 내부 오류가 발생했습니다.");
    
    private final int code;
    private final String message;
}

