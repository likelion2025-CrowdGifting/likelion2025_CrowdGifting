package com.likelion.demoday.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CreateContributionResponse {
    
    private Long contributionId;
    private String orderId;
    private VirtualAccountInfo virtualAccountInfo;
    private PaymentStatusResponse paymentStatus;
}

