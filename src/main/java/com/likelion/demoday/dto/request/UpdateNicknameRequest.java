package com.likelion.demoday.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateNicknameRequest {
    
    @NotBlank(message = "닉네임은 필수입니다.")
    private String nickname;
}

