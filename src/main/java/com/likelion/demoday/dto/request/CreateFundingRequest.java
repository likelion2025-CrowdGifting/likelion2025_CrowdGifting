package com.likelion.demoday.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class CreateFundingRequest {
    
    @NotNull(message = "선물 이미지는 필수입니다.")
    private MultipartFile giftImage;
    
    @NotBlank(message = "펀딩 제목은 필수입니다.")
    private String title;
    
    @NotNull(message = "목표 금액은 필수입니다.")
    @DecimalMin(value = "10000", message = "목표 금액은 최소 10,000원 이상이어야 합니다.")
    private BigDecimal targetAmount;
    
    @NotNull(message = "마감일은 필수입니다.")
    @Future(message = "마감일은 현재 시간 이후여야 합니다.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime deadlineAt;

    private String description;
}

