package com.likelion.demoday.dto.request;

import com.likelion.demoday.domain.enums.Bank;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateBankAccountRequest {
    
    @NotNull(message = "은행은 필수입니다.")
    private Bank bank;
    
    @NotBlank(message = "계좌번호는 필수입니다.")
    private String accountNumber;
}

