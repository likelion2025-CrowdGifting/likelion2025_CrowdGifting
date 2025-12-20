package com.likelion.demoday.service;

import com.likelion.demoday.domain.entity.Funding;
import com.likelion.demoday.domain.enums.FundingStatus;
import com.likelion.demoday.dto.request.CreateFundingRequest;
import com.likelion.demoday.dto.request.StopFundingRequest;
import com.likelion.demoday.dto.response.FundingDetailResponse;
import com.likelion.demoday.dto.response.FundingListResponse;
import io.jsonwebtoken.impl.security.EdwardsCurve;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FundingService {

    // 새 펀딩 생성
    FundingDetailResponse createFunding(Long userId, CreateFundingRequest request, String imageUrl);

    //펀딩 상세 조회 (게스트 참여 내역 포함)
    FundingDetailResponse getFundingDetail(Long fundingId);
    
    // 나의 펀딩 목록 조회 (상태별)
    List<FundingListResponse> getMyFundings(Long userId, FundingStatus status);
    
    // 펀딩 중단
    void stopFunding(Long userId, Long fundingId, StopFundingRequest request);

    void updateExpiredFundings();
}

