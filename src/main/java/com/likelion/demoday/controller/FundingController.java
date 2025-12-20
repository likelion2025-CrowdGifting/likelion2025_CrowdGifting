package com.likelion.demoday.controller;

import com.likelion.demoday.domain.enums.FundingStatus;
import com.likelion.demoday.dto.request.CreateFundingRequest;
import com.likelion.demoday.dto.request.StopFundingRequest;
import com.likelion.demoday.dto.response.FundingDetailResponse;
import com.likelion.demoday.dto.response.FundingListResponse;
import com.likelion.demoday.global.aws.S3Service;
import com.likelion.demoday.global.exception.BusinessException;
import com.likelion.demoday.global.exception.ErrorCode;
import com.likelion.demoday.global.response.BaseResponse;
import com.likelion.demoday.service.FundingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fundings")
@RequiredArgsConstructor
public class FundingController {

    private final FundingService fundingService;
    private final S3Service s3Service;

    @PostMapping
    public ResponseEntity<BaseResponse<FundingDetailResponse>> createFunding(
            Authentication authentication,
            @Valid @ModelAttribute CreateFundingRequest request) {
        Long userId = Long.parseLong(authentication.getName());

        String imageUrl = null;
        try {
            if (request.getGiftImage() != null && !request.getGiftImage().isEmpty()) {
                imageUrl = s3Service.uploadImage(request.getGiftImage());
            }
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SERVER_ERROR);
        }

        FundingDetailResponse response = fundingService.createFunding(userId, request, imageUrl);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success(response));
    }

    // 펀딩 상세 조회
    @GetMapping("/{fundingId}")
    public ResponseEntity<BaseResponse<FundingDetailResponse>> getFundingDetail(@PathVariable Long fundingId) {
        FundingDetailResponse response = fundingService.getFundingDetail(fundingId);

        return ResponseEntity.ok(BaseResponse.success(response));
    }

    // 나의 펀딩 목록 조회
    @GetMapping("/my")
    public ResponseEntity<BaseResponse<List<FundingListResponse>>> getMyFundings(
            Authentication authentication,
            @RequestParam(defaultValue = "IN_PROGRESS") FundingStatus status) {

        if (authentication == null || authentication.getName() == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        Long userId = Long.parseLong(authentication.getName());
        List<FundingListResponse> response = fundingService.getMyFundings(userId, status);

        return ResponseEntity.ok(BaseResponse.success(response));
    }

    // 펀딩 중단 POST
    @PostMapping("/{fundingId}/stop")
    public ResponseEntity<BaseResponse<Void>> stopFunding(
            Authentication authentication,
            @PathVariable Long fundingId,
            @RequestBody(required = false) StopFundingRequest request) {

        Long userId = Long.parseLong(authentication.getName());
        fundingService.stopFunding(userId, fundingId, request);

        return ResponseEntity.ok(BaseResponse.success());
    }
}