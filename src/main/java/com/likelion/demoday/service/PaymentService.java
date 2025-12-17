package com.likelion.demoday.service;

import com.likelion.demoday.dto.request.TossWebhookRequest;

public interface PaymentService {
    
    // 토스페이먼츠 웹훅 처리
    void handleTossWebhook(TossWebhookRequest request);
    
    // 웹훅 서명 검증
    boolean verifyWebhookSignature(String payload, String signature);
}

