package com.likelion.demoday.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateContributionRequest {
    
    @NotBlank(message = "펀딩 닉네임은 필수입니다.")
    private String guestNickname;
    
    @NotNull(message = "참여 금액은 필수입니다.")
    @DecimalMin(value = "1000", message = "참여 금액은 최소 1,000원 이상이어야 합니다.")
    private BigDecimal amount;
    
    private String message; // 선택사항
}

