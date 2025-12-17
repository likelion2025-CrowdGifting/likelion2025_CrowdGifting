package com.likelion.demoday.domain.repository;

import com.likelion.demoday.domain.entity.Contribution;
import com.likelion.demoday.domain.entity.Funding;
import com.likelion.demoday.domain.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContributionRepository extends JpaRepository<Contribution, Long> {
    
    // 펀딩의 참여 내역 조회 (결제 완료된 것만)
    @Query("SELECT c FROM Contribution c WHERE c.funding = :funding AND c.paymentStatus = :status ORDER BY c.createdAt DESC")
    List<Contribution> findByFundingAndPaymentStatus(@Param("funding") Funding funding, 
                                                     @Param("status") PaymentStatus status);
    
    // 펀딩의 모든 참여 내역 조회
    List<Contribution> findByFundingOrderByCreatedAtDesc(Funding funding);
    
    // 주문번호로 조회 (멱등성 검증용)
    Optional<Contribution> findByOrderId(String orderId);
    
    //토스 키로 조회 (webhook 멱등성 검증용)
    Optional<Contribution> findByTossKey(String tossKey);
    
    // 펀딩의 결제 완료된 총 금액 합계
    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM Contribution c WHERE c.funding = :funding AND c.paymentStatus = :status")
    java.math.BigDecimal sumAmountByFundingAndStatus(@Param("funding") Funding funding, 
                                                     @Param("status") PaymentStatus status);
}

