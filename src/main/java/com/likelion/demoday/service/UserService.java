package com.likelion.demoday.service;

import com.likelion.demoday.dto.request.UpdateBankAccountRequest;
import com.likelion.demoday.dto.request.UpdateNicknameRequest;
import com.likelion.demoday.dto.response.UserResponse;

public interface UserService {
    
    // 현재 로그인한 사용자 정보 조회
    UserResponse getMyInfo(Long userId);
    
    // 닉네임 수정
    UserResponse updateNickname(Long userId, UpdateNicknameRequest request);
    
    // 계좌 정보 수정
    UserResponse updateBankAccount(Long userId, UpdateBankAccountRequest request);
}

