package com.likelion.demoday.service;

import com.likelion.demoday.dto.request.CreateContributionRequest;
import com.likelion.demoday.dto.response.ContributionResponse;
import com.likelion.demoday.dto.response.CreateContributionResponse;
import com.likelion.demoday.dto.response.PaymentStatusResponse;


public interface ContributionService {
    
    // 펀딩 참여 생성 및 가상계좌 발급
    CreateContributionResponse createContribution(Long fundingId, CreateContributionRequest request);
    
    // 참여 및 결제 상태 조회
    PaymentStatusResponse getContributionStatus(Long contributionId);
    
    // 참여 상세 조회
    ContributionResponse getContributionDetail(Long contributionId);
}

