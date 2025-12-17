package com.likelion.demoday.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TossWebhookRequest {
    
    private String eventType; // "PAYMENT_CONFIRMED", "PAYMENT_FAILED", "PAYMENT_CANCELED" 등
    private String paymentKey;
    private String orderId;
    private String status; // "DONE", "FAILED", "CANCELED" 등
    private String secret; // 웹훅 시크릿 (서명 검증용)
    private Long totalAmount;
    private String transactionKey; // 토스 고유 키 (멱등성 보장용)
}

