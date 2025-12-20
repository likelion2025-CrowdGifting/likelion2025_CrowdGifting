package com.likelion.demoday.controller;

import com.likelion.demoday.dto.request.PaymentCompleteRequest;
import com.likelion.demoday.global.response.BaseResponse;
import com.likelion.demoday.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    // 결제 검증 및 완료 처리
    @PostMapping("/complete")
    public ResponseEntity<BaseResponse<Void>> completePayment(@RequestBody PaymentCompleteRequest request) {
        log.info("포트원 결제 검증 요청: impUid={}, merchantUid={}", request.getImpUid(), request.getMerchantUid());

        paymentService.verifyAndCompletePayment(request);

        return ResponseEntity.ok(BaseResponse.success("결제가 정상적으로 완료되었습니다.", null));
    }
}