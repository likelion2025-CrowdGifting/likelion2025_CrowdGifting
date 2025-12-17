package com.likelion.demoday.dto.request;

import com.likelion.demoday.domain.enums.Bank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;
    
    @NotBlank(message = "비밀번호는 필수입니다.")
    // 정규표현식으로 비밀번호 규칙 강제로 정함
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$", 
             message = "비밀번호는 최소 8자 이상, 영문과 숫자를 포함해야 합니다.")
    private String password;
    
    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String passwordConfirm;
    
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;
    
    @NotNull(message = "은행은 필수입니다.")
    private Bank bank;
    
    @NotBlank(message = "계좌번호는 필수입니다.")
    private String accountNumber;
}

