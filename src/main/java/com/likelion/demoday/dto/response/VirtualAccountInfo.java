package com.likelion.demoday.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class VirtualAccountInfo {
    
    private String bank; // 은행명
    private String accountNumber; // 계좌번호
    private String accountHolder; // 예금주
    private LocalDateTime dueDate; // 입금기한
}

