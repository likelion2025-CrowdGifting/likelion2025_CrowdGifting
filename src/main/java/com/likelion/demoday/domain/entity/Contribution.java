package com.likelion.demoday.domain.entity;

import com.likelion.demoday.domain.enums.PaymentProvider;
import com.likelion.demoday.domain.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "contributions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Contribution {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funding_id", nullable = false)
    private Funding funding;

    @Column(name = "guest_nickname", nullable = false)
    private String guestNickname;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(length = 1000)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_provider", nullable = false)
    private PaymentProvider paymentProvider;

    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;

    @Column(name = "payment_key")
    private String paymentKey; // 토스페이먼츠 payment key

    @Column(name = "toss_key")
    private String tossKey; // 토스페이먼츠 고유 키 (webhook idempotency용)

    @Column(name = "virtual_account_info", columnDefinition = "TEXT")
    private String virtualAccountInfo; // JSON 형태로 저장 (은행, 계좌번호, 예금주, 입금기한 등)

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        paymentStatus = PaymentStatus.READY;
    }

    @Builder
    public Contribution(Funding funding, String guestNickname, BigDecimal amount, String message,
                       PaymentProvider paymentProvider, String orderId) {
        this.funding = funding;
        this.guestNickname = guestNickname;
        this.amount = amount;
        this.message = message;
        this.paymentProvider = paymentProvider;
        this.orderId = orderId;
    }

    // 결제 완료 처리
    public void completePayment(String paymentKey, String tossKey, String virtualAccountInfo) {
        this.paymentStatus = PaymentStatus.DONE;
        this.paymentKey = paymentKey;
        this.tossKey = tossKey;
        this.virtualAccountInfo = virtualAccountInfo;
        this.paidAt = LocalDateTime.now();
    }

    public void updatePaymentStatus(PaymentStatus status) {
        this.paymentStatus = status;
    }

    // 결제 실패 처리
    public void failPayment() {
        this.paymentStatus = PaymentStatus.FAILED;
    }

    //결제 취소 처리
    public void cancelPayment() {
        this.paymentStatus = PaymentStatus.CANCELED;
    }

    // 가상계좌 정보 업데이트 (발급 시)
    public void updateVirtualAccountInfo(String virtualAccountInfo) {
        this.virtualAccountInfo = virtualAccountInfo;
    }
}

