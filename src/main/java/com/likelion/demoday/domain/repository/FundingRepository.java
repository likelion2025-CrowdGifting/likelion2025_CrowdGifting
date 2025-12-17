package com.likelion.demoday.domain.repository;

import com.likelion.demoday.domain.entity.Funding;
import com.likelion.demoday.domain.entity.User;
import com.likelion.demoday.domain.enums.FundingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FundingRepository extends JpaRepository<Funding, Long> {
    
    //사용자의 펀딩 목록 조회 (상태별)
    List<Funding> findByOwnerAndStatusOrderByCreatedAtDesc(User owner, FundingStatus status);
    
    // 사용자의 진행 중인 펀딩 목록 조회
    @Query("SELECT f FROM Funding f WHERE f.owner = :owner AND f.status = :status AND f.deadlineAt > :now ORDER BY f.createdAt DESC")
    List<Funding> findInProgressByOwner(@Param("owner") User owner, 
                                        @Param("status") FundingStatus status, 
                                        @Param("now") LocalDateTime now);
    
    //사용자의 종료된 펀딩 목록 조회
    @Query("SELECT f FROM Funding f WHERE f.owner = :owner AND f.status IN :statuses ORDER BY f.createdAt DESC")
    List<Funding> findEndedByOwner(@Param("owner") User owner, 
                                   @Param("statuses") List<FundingStatus> statuses);
    
    // 펀딩 상세 조회 (게스트 참여 내역 포함)
    @Query("SELECT DISTINCT f FROM Funding f LEFT JOIN FETCH f.contributions WHERE f.id = :id")
    Optional<Funding> findByIdWithContributions(@Param("id") Long id);
    
    //마감일이 지난 진행 중 펀딩 조회 (배치 처리용)
    @Query("SELECT f FROM Funding f WHERE f.status = :status AND f.deadlineAt <= :now")
    List<Funding> findExpiredFundings(@Param("status") FundingStatus status, 
                                      @Param("now") LocalDateTime now);
}

