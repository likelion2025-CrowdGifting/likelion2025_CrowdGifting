package com.likelion.demoday.service.impl;

import com.likelion.demoday.dto.response.VirtualAccountInfo;
import com.likelion.demoday.service.TossPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// 토스페이먼츠 결제 서비스 구현

 // * TODO: Toss Payments 키 발급 후 활성화 예정
@Slf4j
// @Service  // Toss Payments 키 발급 전까지 비활성화
@RequiredArgsConstructor
public class TossPaymentServiceImpl implements TossPaymentService {
    
    @Value("${toss.payments.secret-key}")
    private String secretKey;
    
    @Value("${toss.payments.base-url}")
    private String baseUrl;
    
    private final RestTemplate restTemplate;
    
    @Override
    public VirtualAccountInfo createVirtualAccount(String orderId, BigDecimal amount, String customerName) {
        // TODO: 실제 토스페이먼츠 API 호출 구현

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + secretKey);
        headers.set("Content-Type", "application/json");
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("amount", amount.intValue());
        requestBody.put("orderId", orderId);
        requestBody.put("orderName", "펀딩 참여");
        requestBody.put("customerName", customerName);
        requestBody.put("virtualAccount", Map.of(
                "validHours", 168, // 7일
                "accountType", "일반"
        ));
        
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    baseUrl + "/v1/virtual-accounts",
                    HttpMethod.POST,
                    request,
                    Map.class
            );
            
            // 응답 파싱 (실제 토스페이먼츠 응답 구조에 맞게 수정 필요)
            Map<String, Object> virtualAccount = (Map<String, Object>) response.getBody().get("virtualAccount");
            
            return VirtualAccountInfo.builder()
                    .bank((String) virtualAccount.get("bank"))
                    .accountNumber((String) virtualAccount.get("accountNumber"))
                    .accountHolder((String) virtualAccount.get("accountHolder"))
                    .dueDate(LocalDateTime.now().plusDays(7))
                    .build();
                    
        } catch (Exception e) {
            log.error("토스페이먼츠 가상계좌 발급 실패: {}", e.getMessage());
            throw new RuntimeException("가상계좌 발급에 실패했습니다.", e);
        }
    }
}

