package com.likelion.demoday.dto.response;

import com.likelion.demoday.domain.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ContributionResponse {
    
    private Long id;
    private String guestNickname;
    private BigDecimal amount;
    private String message;
    private PaymentStatus paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime paidAt;
    
    // 가상계좌 정보 (입금 대기 중일 때만)
    private VirtualAccountInfo virtualAccountInfo;
}

