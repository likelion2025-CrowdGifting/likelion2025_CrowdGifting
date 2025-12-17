package com.likelion.demoday.dto.response;

import com.likelion.demoday.domain.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PaymentStatusResponse {
    
    private PaymentStatus status;
    private String message;
}

