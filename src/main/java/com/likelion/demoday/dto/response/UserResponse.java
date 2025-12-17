package com.likelion.demoday.dto.response;

import com.likelion.demoday.domain.enums.Bank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class UserResponse {
    
    private Long id;
    private String email;
    private String nickname;
    private Bank bank;
    private String accountNumber;
    private LocalDateTime createdAt;
}

