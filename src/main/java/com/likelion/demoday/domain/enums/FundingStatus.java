package com.likelion.demoday.domain.enums;

public enum FundingStatus {
    // 진행 중인 펀딩
    IN_PROGRESS,
    
    // 종료된 펀딩 (100% 달성)
    ENDED_SUCCESS,
    
    //사용자가 중단한 펀딩
    ENDED_STOPPED,
    
    //마감일 경과로 만료된 펀딩
    ENDED_EXPIRED
}