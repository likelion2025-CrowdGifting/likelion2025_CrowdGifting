package com.likelion.demoday.controller;

import com.likelion.demoday.domain.enums.FundingStatus;
import com.likelion.demoday.dto.request.CreateFundingRequest;
import com.likelion.demoday.dto.request.StopFundingRequest;
import com.likelion.demoday.dto.response.FundingDetailResponse;
import com.likelion.demoday.dto.response.FundingListResponse;
import com.likelion.demoday.service.FundingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/fundings")
@RequiredArgsConstructor
public class FundingController {
    
    private final FundingService fundingService;
    
    // POST /api/v1/fundings
    @PostMapping
    public ResponseEntity<FundingDetailResponse> createFunding(
            Authentication authentication,
            @Valid @ModelAttribute CreateFundingRequest request) {
        Long userId = Long.parseLong(authentication.getName());
        FundingDetailResponse response = fundingService.createFunding(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    //펀딩 상세 조회 GET /api/v1/fundings/{fundingId}
    @GetMapping("/{fundingId}")
    public ResponseEntity<FundingDetailResponse> getFundingDetail(@PathVariable Long fundingId) {
        FundingDetailResponse response = fundingService.getFundingDetail(fundingId);
        return ResponseEntity.ok(response);
    }
    
    //나의 펀딩 목록 조회 GET /api/v1/fundings/my?status={status}
    @GetMapping("/my")
    public ResponseEntity<List<FundingListResponse>> getMyFundings(
            Authentication authentication,
            @RequestParam(defaultValue = "IN_PROGRESS") FundingStatus status) {
        Long userId = Long.parseLong(authentication.getName());
        List<FundingListResponse> response = fundingService.getMyFundings(userId, status);
        return ResponseEntity.ok(response);
    }
    
    // 펀딩 중단 POST /api/v1/fundings/{fundingId}/stop
    // body 없이도, 빈 body({})로도, 사유와 함께도 호출 가능
    @PostMapping("/{fundingId}/stop")
    public ResponseEntity<Void> stopFunding(
            Authentication authentication,
            @PathVariable Long fundingId,
            @RequestBody(required = false) StopFundingRequest request) {
        Long userId = Long.parseLong(authentication.getName());
        fundingService.stopFunding(userId, fundingId, request);
        return ResponseEntity.ok().build();
    }
}

