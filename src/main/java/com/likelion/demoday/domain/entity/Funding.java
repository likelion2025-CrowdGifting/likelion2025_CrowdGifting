package com.likelion.demoday.domain.entity;

import com.likelion.demoday.domain.enums.FundingStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "fundings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Funding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "funding", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Contribution> contributions = new ArrayList<>();

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "gift_img_url", nullable = false)
    private String giftImgUrl;

    @Column(name = "target_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "current_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal currentAmount; // 현재 모금액 (결제 완료된 금액만 합산)

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "deadline_at", nullable = false)
    private LocalDateTime deadlineAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FundingStatus status;

    @Column(name = "stop_reason", length = 500)
    private String stopReason;

    @Column(name = "stopped_at")
    private LocalDateTime stoppedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        currentAmount = BigDecimal.ZERO;
        status = FundingStatus.IN_PROGRESS;
    }

    @Builder
    public Funding(User owner, String title, String description, String giftImgUrl, BigDecimal targetAmount, LocalDateTime deadlineAt) {
        this.owner = owner;
        this.title = title;
        this.description = description;
        this.giftImgUrl = giftImgUrl;
        this.targetAmount = targetAmount;
        this.deadlineAt = deadlineAt;
    }

    // 펀딩 모금액 증가 (결제 완료 시 호출)
    public void addAmount(BigDecimal amount) {
        this.currentAmount = this.currentAmount.add(amount);
    }

    // 펀딩 중단 처리
    public void stop(String reason) {
        this.status = FundingStatus.ENDED_STOPPED;
        this.stopReason = (reason == null || reason.isBlank()) ? null : reason;
        this.stoppedAt = LocalDateTime.now();
    }

    // 펀딩 상태 업데이트 (시스템에 의해 호출)
    public void updateStatus(FundingStatus status) {
        this.status = status;
    }

    // 목표 달성률 계산 (퍼센트)
    public double getAchievementRate() {
        if (targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return currentAmount.divide(targetAmount, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    // 남은 금액 계산
    public BigDecimal getRemainingAmount() {
        BigDecimal remaining = targetAmount.subtract(currentAmount);
        return remaining.compareTo(BigDecimal.ZERO) > 0 ? remaining : BigDecimal.ZERO;
    }

    // 100% 달성 여부 확인
    public boolean isAchieved() {
        return currentAmount.compareTo(targetAmount) >= 0;
    }
}

