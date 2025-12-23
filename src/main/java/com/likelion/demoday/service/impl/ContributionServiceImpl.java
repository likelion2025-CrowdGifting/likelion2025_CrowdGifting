package com.likelion.demoday.service.impl;

import com.likelion.demoday.domain.entity.Contribution;
import com.likelion.demoday.domain.entity.Funding;
import com.likelion.demoday.domain.enums.FundingStatus;
import com.likelion.demoday.domain.enums.PaymentProvider;
import com.likelion.demoday.domain.enums.PaymentStatus;
import com.likelion.demoday.domain.repository.ContributionRepository;
import com.likelion.demoday.domain.repository.FundingRepository;
import com.likelion.demoday.dto.request.CreateContributionRequest;
import com.likelion.demoday.dto.response.ContributionResponse;
import com.likelion.demoday.dto.response.CreateContributionResponse;
import com.likelion.demoday.dto.response.PaymentStatusResponse;
import com.likelion.demoday.dto.response.VirtualAccountInfo;
import com.likelion.demoday.global.exception.BusinessException;
import com.likelion.demoday.global.exception.ErrorCode;
import com.likelion.demoday.service.ContributionService;
import com.likelion.demoday.service.TossPaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class ContributionServiceImpl implements ContributionService {
    
    private final ContributionRepository contributionRepository;
    private final FundingRepository fundingRepository;
    // TODO: Toss Payments 키 발급 후 활성화
    // private final TossPaymentService tossPaymentService;
    
    @Override
    @Transactional
    public CreateContributionResponse createContribution(Long fundingId, CreateContributionRequest request) {
        Funding funding = fundingRepository.findById(fundingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FUNDING_NOT_FOUND));
        
        // 펀딩 상태 검증 (진행 중인 펀딩만 참여 가능)
        if (funding.getStatus() != FundingStatus.IN_PROGRESS) {
            throw new BusinessException(ErrorCode.FUNDING_EXPIRED);
        }
        
        // 마감일 검증
        if (funding.getDeadlineAt().isBefore(java.time.LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.FUNDING_EXPIRED);
        }
        
        // 참여 금액 검증 (최소 1,000원)
        if (request.getAmount().compareTo(BigDecimal.valueOf(1000)) < 0) {
            throw new BusinessException(ErrorCode.INVALID_CONTRIBUTION_AMOUNT);
        }
        
        // 결제 금액 검증: 남은 금액 초과 시 참여 거부
        BigDecimal remainingAmount = funding.getRemainingAmount();
        if (request.getAmount().compareTo(remainingAmount) > 0) {
            throw new BusinessException(ErrorCode.PAYMENT_AMOUNT_EXCEEDS_REMAINING);
        }
        
        // 주문번호 생성 (멱등성 보장용)
        String orderId = generateOrderId(fundingId);
        
        // Contribution 엔티티 생성 (입금 대기 상태)
        Contribution contribution = Contribution.builder()
                .funding(funding)
                .guestNickname(request.getGuestNickname())
                .amount(request.getAmount())
                .message(request.getMessage())
                .paymentProvider(PaymentProvider.TOSS)
                .orderId(orderId)
                .build();
        
        contribution = contributionRepository.save(contribution);
        
        // TODO: Toss Payments 키 발급 후 활성화
        // 토스페이먼츠 가상계좌 발급 API 호출
        // VirtualAccountInfo virtualAccountInfo = tossPaymentService.createVirtualAccount(
        //         orderId,
        //         request.getAmount(),
        //         request.getGuestNickname()
        // );
        
        // 임시 더미 가상계좌 정보 (Toss Payments 연동 전까지)
        VirtualAccountInfo virtualAccountInfo = createDummyVirtualAccount();
        
        // 가상계좌 정보 저장
        contribution.updateVirtualAccountInfo(convertToJson(virtualAccountInfo));
        contribution = contributionRepository.save(contribution);
        
        return CreateContributionResponse.builder()
                .contributionId(contribution.getId())
                .orderId(contribution.getOrderId())
                .virtualAccountInfo(virtualAccountInfo)
                .paymentStatus(PaymentStatusResponse.builder()
                        .status(PaymentStatus.READY)
                        .message("입금 대기 중")
                        .build())
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public PaymentStatusResponse getContributionStatus(Long contributionId) {
        Contribution contribution = contributionRepository.findById(contributionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTRIBUTION_NOT_FOUND));
        
        String message = switch (contribution.getPaymentStatus()) {
            case READY -> "입금 대기 중";
            case DONE -> "결제 완료";
            case FAILED -> "결제 실패";
            case CANCELED -> "결제 취소";
        };
        
        return PaymentStatusResponse.builder()
                .status(contribution.getPaymentStatus())
                .message(message)
                .build();
    }
    
    @Override
    @Transactional(readOnly = true)
    public ContributionResponse getContributionDetail(Long contributionId) {
        Contribution contribution = contributionRepository.findById(contributionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTRIBUTION_NOT_FOUND));
        
        VirtualAccountInfo virtualAccountInfo = null;
        if (contribution.getPaymentStatus() == PaymentStatus.READY 
                && contribution.getVirtualAccountInfo() != null) {
            virtualAccountInfo = parseVirtualAccountInfo(contribution.getVirtualAccountInfo());
        }
        
        return ContributionResponse.builder()
                .id(contribution.getId())
                .guestNickname(contribution.getGuestNickname())
                .amount(contribution.getAmount())
                .message(contribution.getMessage())
                .paymentStatus(contribution.getPaymentStatus())
                .createdAt(contribution.getCreatedAt())
                .paidAt(contribution.getPaidAt())
                .virtualAccountInfo(virtualAccountInfo)
                .build();
    }
    
    /**
     * 주문번호 생성 (멱등성 보장용)
     */
    private String generateOrderId(Long fundingId) {
        return "FUNDING_" + fundingId + "_" + UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * VirtualAccountInfo를 JSON 문자열로 변환
     */
    private String convertToJson(VirtualAccountInfo info) {
        // TODO: 실제 JSON 변환 로직 구현 (Jackson ObjectMapper 사용)
        return String.format(
                "{\"bank\":\"%s\",\"accountNumber\":\"%s\",\"accountHolder\":\"%s\",\"dueDate\":\"%s\"}",
                info.getBank(), info.getAccountNumber(), info.getAccountHolder(), info.getDueDate()
        );
    }
    
    /**
     * JSON 문자열을 VirtualAccountInfo로 파싱
     */
    private VirtualAccountInfo parseVirtualAccountInfo(String json) {
        // TODO: 실제 JSON 파싱 로직 구현 (Jackson ObjectMapper 사용)
        // 간단한 예시로 반환
        return VirtualAccountInfo.builder()
                .bank("은행명")
                .accountNumber("계좌번호")
                .accountHolder("예금주")
                .dueDate(java.time.LocalDateTime.now().plusDays(7))
                .build();
    }
    
    /**
     * 임시 더미 가상계좌 정보 생성 (Toss Payments 연동 전까지)
     */
    private VirtualAccountInfo createDummyVirtualAccount() {
        return VirtualAccountInfo.builder()
                .bank("토스뱅크")
                .accountNumber("1234-5678-9012")
                .accountHolder("펀딩시스템")
                .dueDate(java.time.LocalDateTime.now().plusDays(7))
                .build();
    }
}

