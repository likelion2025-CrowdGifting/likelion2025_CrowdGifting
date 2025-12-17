package com.likelion.demoday.dto.response;

import com.likelion.demoday.domain.enums.FundingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class FundingDetailResponse {
    
    private Long id;
    private String title;
    private String giftImgUrl;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private BigDecimal remainingAmount;
    private double achievementRate; // 퍼센트
    private LocalDateTime createdAt;
    private LocalDateTime deadlineAt;
    private FundingStatus status;
    private Long remainingDays; // D-day (null 가능)
    
    // 유저 정보
    private String ownerNickname;
    
    // 게스트 참여 내역 (결제 완료된 것만)
    private List<ContributionResponse> contributions;
}

