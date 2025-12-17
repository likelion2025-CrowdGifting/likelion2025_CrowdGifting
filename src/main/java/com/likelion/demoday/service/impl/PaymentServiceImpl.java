package com.likelion.demoday.service.impl;

import com.likelion.demoday.domain.entity.Contribution;
import com.likelion.demoday.domain.entity.Funding;
import com.likelion.demoday.domain.enums.PaymentStatus;
import com.likelion.demoday.domain.repository.ContributionRepository;
import com.likelion.demoday.dto.request.TossWebhookRequest;
import com.likelion.demoday.global.exception.BusinessException;
import com.likelion.demoday.global.exception.ErrorCode;
import com.likelion.demoday.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


 // * TODO: Toss Payments 키 발급 후 활성화 예정

@Slf4j
// @Service  // Toss Payments 키 발급 전까지 비활성화
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    
    @Value("${toss.webhook-secret}")
    private String webhookSecret;
    
    private final ContributionRepository contributionRepository;
    
    @Override
    @Transactional
    public void handleTossWebhook(TossWebhookRequest request) {
        // 1. 웹훅 서명 검증
        // TODO: 실제 토스페이먼츠 웹훅 서명 검증 로직 구현
        // verifyWebhookSignature(request);
        
        // 2. 멱등성 검증 - 토스 키로 중복 확인
        if (request.getTransactionKey() != null) {
            contributionRepository.findByTossKey(request.getTransactionKey())
                    .ifPresent(existing -> {
                        log.info("중복된 웹훅 요청 무시: transactionKey={}", request.getTransactionKey());
                        throw new BusinessException(ErrorCode.DUPLICATE_WEBHOOK);
                    });
        }
        
        // 3. 주문번호로 Contribution 조회
        Contribution contribution = contributionRepository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTRIBUTION_NOT_FOUND));
        
        // 4. 이미 처리된 결제인지 확인 (추가 멱등성 보장)
        if (contribution.getPaymentStatus() == PaymentStatus.DONE) {
            log.info("이미 처리된 결제: orderId={}", request.getOrderId());
            return; // 이미 처리됨 - 정상 응답 반환 (멱등성)
        }
        
        // 5. 결제 상태에 따라 처리
        Funding funding = contribution.getFunding();
        
        switch (request.getStatus().toUpperCase()) {
            case "DONE":
                // 결제 완료 처리
                contribution.completePayment(
                        request.getPaymentKey(),
                        request.getTransactionKey(),
                        contribution.getVirtualAccountInfo()
                );
                
                // 펀딩 모금액 증가
                funding.addAmount(contribution.getAmount());
                
                // 100% 달성 여부 확인 및 상태 업데이트
                if (funding.isAchieved()) {
                    funding.updateStatus(com.likelion.demoday.domain.enums.FundingStatus.ENDED_SUCCESS);
                }
                
                break;
                
            case "FAILED":
                contribution.failPayment();
                break;
                
            case "CANCELED":
                contribution.cancelPayment();
                // 이미 합산된 금액이 있다면 차감 (환불 처리)
                if (contribution.getPaymentStatus() == PaymentStatus.DONE) {
                    funding.addAmount(contribution.getAmount().negate());
                }
                break;
                
            default:
                log.warn("알 수 없는 결제 상태: {}", request.getStatus());
        }
        
        contributionRepository.save(contribution);
        log.info("웹훅 처리 완료: orderId={}, status={}", request.getOrderId(), request.getStatus());
    }
    
    @Override
    public boolean verifyWebhookSignature(String payload, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                    webhookSecret.getBytes(StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            mac.init(secretKeySpec);
            
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            String computedSignature = Base64.getEncoder().encodeToString(hash);
            
            return computedSignature.equals(signature);
        } catch (Exception e) {
            log.error("웹훅 서명 검증 실패: {}", e.getMessage());
            return false;
        }
    }
}

