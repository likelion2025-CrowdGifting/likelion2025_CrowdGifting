package com.likelion.demoday.controller;

import com.likelion.demoday.dto.request.CreateContributionRequest;
import com.likelion.demoday.dto.response.ContributionResponse;
import com.likelion.demoday.dto.response.CreateContributionResponse;
import com.likelion.demoday.dto.response.PaymentStatusResponse;
import com.likelion.demoday.service.ContributionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/fundings/{fundingId}/contributions")
@RequiredArgsConstructor
public class ContributionController {
    
    private final ContributionService contributionService;
    
    // 펀딩 참여 생성 + 가상계좌 발급 POST /api/v1/fundings/{fundingId}/contributions

    @PostMapping
    public ResponseEntity<CreateContributionResponse> createContribution(
            @PathVariable Long fundingId,
            @Valid @RequestBody CreateContributionRequest request) {
        CreateContributionResponse response = contributionService.createContribution(fundingId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    // 참여 및 결제 상태 조회 GET /api/v1/fundings/{fundingId}/contributions/{id}
    @GetMapping("/{id}")
    public ResponseEntity<PaymentStatusResponse> getContributionStatus(@PathVariable Long id) {
        PaymentStatusResponse response = contributionService.getContributionStatus(id);
        return ResponseEntity.ok(response);
    }
    
    // 참여 상세 조회 GET /api/v1/fundings/{fundingId}/contributions/{id}/detail
    @GetMapping("/{id}/detail")
    public ResponseEntity<ContributionResponse> getContributionDetail(@PathVariable Long id) {
        ContributionResponse response = contributionService.getContributionDetail(id);
        return ResponseEntity.ok(response);
    }
}

