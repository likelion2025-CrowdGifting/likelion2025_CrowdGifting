package com.likelion.demoday.domain.enums;

// 은행 Enum
public enum Bank {
    KB("KB국민은행"),
    SHINHAN("신한은행"),
    WOORI("우리은행"),
    IBK("IBK기업은행"),
    KAKAO("카카오뱅크"),
    TOSS("토스뱅크");

    // figma에 없는 은행들
//    KDB("KDB산업은행"),
//    SUHYUP("수협은행"),
//    HANA("하나은행"),
//    NH("NH농협은행"),

    private final String displayName;

    Bank(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

