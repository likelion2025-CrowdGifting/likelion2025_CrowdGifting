package com.likelion.demoday.dto.response;

import com.likelion.demoday.domain.enums.FundingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class FundingListResponse {
    
    private Long id;
    private String title;
    private String giftImgUrl;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private double achievementRate; // 퍼센트
    private LocalDateTime createdAt;
    private LocalDateTime deadlineAt;
    private FundingStatus status;
    private Long remainingDays; // D-day (진행 중일 때만)
    private String stopReason; // 중단된 경우
}

