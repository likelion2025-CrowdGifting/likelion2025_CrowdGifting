package com.likelion.demoday.controller;

import com.likelion.demoday.dto.request.TossWebhookRequest;
import com.likelion.demoday.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@Slf4j
// @RestController  // Toss Payments 키 발급 전까지 비활성화
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;
    
    // 토스페이먼츠 웹훅 수신 POST /api/v1/payments/toss/webhook
    @PostMapping("/toss/webhook")
    public ResponseEntity<Void> handleTossWebhook(@RequestBody TossWebhookRequest request) {
        log.info("토스페이먼츠 웹훅 수신: orderId={}, status={}", request.getOrderId(), request.getStatus());
        
        paymentService.handleTossWebhook(request);
        
        return ResponseEntity.ok().build();
    }
}

