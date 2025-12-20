package com.likelion.demoday.service.impl;

import com.likelion.demoday.domain.entity.Contribution;
import com.likelion.demoday.domain.entity.Funding;
import com.likelion.demoday.domain.enums.FundingStatus;
import com.likelion.demoday.domain.enums.PaymentStatus;
import com.likelion.demoday.domain.repository.ContributionRepository;
import com.likelion.demoday.domain.repository.FundingRepository;
import com.likelion.demoday.dto.request.PaymentCompleteRequest;
import com.likelion.demoday.global.exception.BusinessException;
import com.likelion.demoday.global.exception.ErrorCode;
import com.likelion.demoday.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final ContributionRepository contributionRepository;
    private final FundingRepository fundingRepository;

    @Value("${portone.api.key}")
    private String apiKey;

    @Value("${portone.api.secret}")
    private String apiSecret;

    @Value("${portone.api.url}")
    private String apiUrl;

    @Override
    @Transactional
    public void verifyAndCompletePayment(PaymentCompleteRequest request) {
        // 1. 주문번호(merchant_uid)로 Contribution 조회
        // 프론트에서 merchant_uid에 contributionId를 담아서 보냈다고 가정
        Long contributionId = Long.parseLong(request.getMerchantUid());
        Contribution contribution = contributionRepository.findById(contributionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTRIBUTION_NOT_FOUND));

        // 2. 이미 처리된 결제인지 확인
        if (contribution.getPaymentStatus() == PaymentStatus.DONE) {
            log.info("이미 처리된 결제입니다. merchantUid={}", request.getMerchantUid());
            return;
        }

        // 3. 포트원 액세스 토큰 발급
        String accessToken = getPortOneAccessToken();

        // 4. 포트원 결제 정보 조회
        Map<String, Object> paymentData = getPaymentData(request.getImpUid(), accessToken);

        // 5. 결제 상태 확인
        String status = (String) paymentData.get("status");
        if (!"paid".equals(status)) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_PAID); // 결제가 완료되지 않음
        }

        // 6. 금액 검증
        BigDecimal portOneAmount = new BigDecimal(String.valueOf(paymentData.get("amount")));
        if (contribution.getAmount().compareTo(portOneAmount) != 0) {
            log.error("결제 금액 불일치: DB={}, PortOne={}", contribution.getAmount(), portOneAmount);
            throw new BusinessException(ErrorCode.PAYMENT_AMOUNT_MISMATCH); // 금액 위변조 의심
        }

        // 7. 검증 통과 -> 결제 완료 처리 로직 실행
        processPaymentSuccess(contribution, request.getImpUid());
    }

    private void processPaymentSuccess(Contribution contribution, String impUid) {

        contribution.updatePaymentStatus(PaymentStatus.DONE);
        Funding funding = contribution.getFunding();

        // 펀딩 모금액 증가
        funding.addAmount(contribution.getAmount());

        // 100% 달성 여부 확인 및 상태 업데이트
        if (funding.isAchieved()) {
            funding.updateStatus(FundingStatus.ENDED_SUCCESS);
        }

        // 변경사항 저장
        fundingRepository.save(funding);
        contributionRepository.save(contribution);

        log.info("결제 처리 완료: contributionId={}, impUid={}", contribution.getId(), impUid);
    }

    private String getPortOneAccessToken() {
        RestClient restClient = RestClient.create();

        try {
            Map<String, Object> response = restClient.post()
                    .uri(apiUrl + "/users/getToken")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("imp_key", apiKey, "imp_secret", apiSecret))
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            Map<String, Object> responseBody = (Map<String, Object>) response.get("response");
            return (String) responseBody.get("access_token");
        } catch (Exception e) {
            log.error("포트원 토큰 발급 실패", e);
            throw new BusinessException(ErrorCode.SERVER_ERROR);
        }
    }

    private Map<String, Object> getPaymentData(String impUid, String accessToken) {
        RestClient restClient = RestClient.create();

        try {
            Map<String, Object> response = restClient.get()
                    .uri(apiUrl + "/payments/" + impUid)
                    .header("Authorization", accessToken)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            return (Map<String, Object>) response.get("response");
        } catch (Exception e) {
            log.error("포트원 결제 정보 조회 실패: impUid={}", impUid, e);
            throw new BusinessException(ErrorCode.PAYMENT_NOT_FOUND);
        }
    }
}