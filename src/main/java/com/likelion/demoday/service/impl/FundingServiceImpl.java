package com.likelion.demoday.service.impl;

import com.likelion.demoday.domain.entity.Contribution;
import com.likelion.demoday.domain.entity.Funding;
import com.likelion.demoday.domain.entity.User;
import com.likelion.demoday.domain.enums.FundingStatus;
import com.likelion.demoday.domain.enums.PaymentStatus;
import com.likelion.demoday.domain.repository.ContributionRepository;
import com.likelion.demoday.domain.repository.FundingRepository;
import com.likelion.demoday.domain.repository.UserRepository;
import com.likelion.demoday.dto.request.CreateFundingRequest;
import com.likelion.demoday.dto.request.StopFundingRequest;
import com.likelion.demoday.dto.response.ContributionResponse;
import com.likelion.demoday.dto.response.FundingDetailResponse;
import com.likelion.demoday.dto.response.FundingListResponse;
import com.likelion.demoday.dto.response.VirtualAccountInfo;
import com.likelion.demoday.global.aws.S3Service;
import com.likelion.demoday.global.exception.BusinessException;
import com.likelion.demoday.global.exception.ErrorCode;
import com.likelion.demoday.service.FundingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FundingServiceImpl implements FundingService {
    
    private final FundingRepository fundingRepository;
    private final UserRepository userRepository;
    private final ContributionRepository contributionRepository;
    private final S3Service s3Service;
    
    @Override
    @Transactional
    public FundingDetailResponse createFunding(Long userId, CreateFundingRequest request, String imageUrl) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        // 목표 금액 검증 (최소 10,000원)
        if (request.getTargetAmount().compareTo(BigDecimal.valueOf(10000)) < 0) {
            throw new BusinessException(ErrorCode.INVALID_TARGET_AMOUNT);
        }
        
        // 마감일 검증 (현재 시간 이후)
        if (request.getDeadlineAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.INVALID_DEADLINE);
        }
        
        Funding funding = Funding.builder()
                .owner(owner)
                .title(request.getTitle())
                .description(request.getDescription())
                .giftImgUrl(imageUrl)
                .targetAmount(request.getTargetAmount())
                .deadlineAt(request.getDeadlineAt())
                .build();
        
        funding = fundingRepository.save(funding);
        
        return mapToDetailResponse(funding);
    }
    
    @Override
    @Transactional(readOnly = true)
    public FundingDetailResponse getFundingDetail(Long fundingId) {
        Funding funding = fundingRepository.findByIdWithContributions(fundingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FUNDING_NOT_FOUND));
        
        // 펀딩 상태 자동 업데이트
        updateFundingStatusIfNeeded(funding);
        
        return mapToDetailResponse(funding);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FundingListResponse> getMyFundings(Long userId, FundingStatus status) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        List<Funding> fundings;
        if (status == FundingStatus.IN_PROGRESS) {
            fundings = fundingRepository.findInProgressByOwner(owner, status, LocalDateTime.now());
        } else {
            List<FundingStatus> endedStatuses = List.of(
                    FundingStatus.ENDED_SUCCESS, 
                    FundingStatus.ENDED_STOPPED, 
                    FundingStatus.ENDED_EXPIRED
            );
            fundings = fundingRepository.findEndedByOwner(owner, endedStatuses);
        }
        
        return fundings.stream()
                .map(this::mapToListResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void stopFunding(Long userId, Long fundingId, StopFundingRequest request) {
        Funding funding = fundingRepository.findById(fundingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FUNDING_NOT_FOUND));
        
        // 소유자 검증
        if (!funding.getOwner().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.FUNDING_NOT_OWNER);
        }
        
        // 이미 종료된 펀딩인지 검증
        if (funding.getStatus() != FundingStatus.IN_PROGRESS) {
            throw new BusinessException(ErrorCode.FUNDING_ALREADY_ENDED);
        }
        
        // 사유 추출 (body가 none이거나 reason이 없으면 null)
        String reason = null;
        if (request != null) {
            reason = request.getReason();
        }
        
        funding.stop(reason);
        fundingRepository.save(funding);
    }
    
    @Override
    @Transactional
    public void updateExpiredFundings() {
        // IN_PROGRESS 상태인 펀딩만 조회하여 마감일 경과 시 ENDED_EXPIRED로 변경
        // 이미 ENDED_SUCCESS나 ENDED_STOPPED인 펀딩은 변경하지 않음
        List<Funding> expiredFundings = fundingRepository.findExpiredFundings(
                FundingStatus.IN_PROGRESS, 
                LocalDateTime.now()
        );
        
        for (Funding funding : expiredFundings) {
            // 한 번 더 확인: IN_PROGRESS 상태인 경우에만 변경
            if (funding.getStatus() == FundingStatus.IN_PROGRESS) {
                funding.updateStatus(FundingStatus.ENDED_EXPIRED);
                fundingRepository.save(funding);
            }
        }
    }
    
    // 펀딩 상태 자동 업데이트 (조회 시점)
    // 우선순위: 중단된 펀딩은 그대로 유지 -> 목표 금액 달성 시 ENDED_SUCCESS -> 마감 기한 경과 시 ENDED_EXPIRED
    private void updateFundingStatusIfNeeded(Funding funding) {
        if (funding.getStatus() != FundingStatus.IN_PROGRESS) {
            return; // 이미 종료된 펀딩은 변경하지 않음
        }

        // 목표 금액 달성 시 성공 처리
        if (funding.isAchieved()) {
            funding.updateStatus(FundingStatus.ENDED_SUCCESS);
            fundingRepository.save(funding);
            return;
        }

        // 마감일 경과 시 만료 처리 (목표 미달성 상태만 해당)
        if (funding.getDeadlineAt().isBefore(LocalDateTime.now())) {
            funding.updateStatus(FundingStatus.ENDED_EXPIRED);
            fundingRepository.save(funding);
        }
    }
    
    // Funding 엔티티를 FundingDetailResponse로 변환
    private FundingDetailResponse mapToDetailResponse(Funding funding) {
        List<Contribution> completedContributions = contributionRepository
                .findByFundingAndPaymentStatus(funding, PaymentStatus.DONE);
        
        List<ContributionResponse> contributionResponses = completedContributions.stream()
                .map(this::mapToContributionResponse)
                .collect(Collectors.toList());
        
        Long remainingDays = null;
        if (funding.getStatus() == FundingStatus.IN_PROGRESS 
                && funding.getDeadlineAt().isAfter(LocalDateTime.now())) {
            remainingDays = ChronoUnit.DAYS.between(LocalDateTime.now(), funding.getDeadlineAt());
        }
        
        return FundingDetailResponse.builder()
                .id(funding.getId())
                .title(funding.getTitle())
                .giftImgUrl(funding.getGiftImgUrl())
                .targetAmount(funding.getTargetAmount())
                .currentAmount(funding.getCurrentAmount())
                .remainingAmount(funding.getRemainingAmount())
                .achievementRate(funding.getAchievementRate())
                .createdAt(funding.getCreatedAt())
                .deadlineAt(funding.getDeadlineAt())
                .status(funding.getStatus())
                .remainingDays(remainingDays)
                .ownerNickname(funding.getOwner().getNickname())
                .contributions(contributionResponses)
                .build();
    }
    
    // Funding 엔티티를 FundingListResponse로 변환
    private FundingListResponse mapToListResponse(Funding funding) {
        Long remainingDays = null;
        if (funding.getStatus() == FundingStatus.IN_PROGRESS 
                && funding.getDeadlineAt().isAfter(LocalDateTime.now())) {
            remainingDays = ChronoUnit.DAYS.between(LocalDateTime.now(), funding.getDeadlineAt());
        }
        
        return FundingListResponse.builder()
                .id(funding.getId())
                .title(funding.getTitle())
                .giftImgUrl(funding.getGiftImgUrl())
                .targetAmount(funding.getTargetAmount())
                .currentAmount(funding.getCurrentAmount())
                .achievementRate(funding.getAchievementRate())
                .createdAt(funding.getCreatedAt())
                .deadlineAt(funding.getDeadlineAt())
                .status(funding.getStatus())
                .remainingDays(remainingDays)
                .stopReason(funding.getStopReason())
                .build();
    }
    
    // Contribution 엔티티를 ContributionResponse로 변환
    private ContributionResponse mapToContributionResponse(Contribution contribution) {
        return ContributionResponse.builder()
                .id(contribution.getId())
                .guestNickname(contribution.getGuestNickname())
                .amount(contribution.getAmount())
                .message(contribution.getMessage())
                .paymentStatus(contribution.getPaymentStatus())
                .createdAt(contribution.getCreatedAt())
                .paidAt(contribution.getPaidAt())
                .build();
    }
}

