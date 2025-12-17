package com.likelion.demoday.service;

import com.likelion.demoday.dto.request.LoginRequest;
import com.likelion.demoday.dto.request.SignupRequest;
import com.likelion.demoday.dto.response.AuthResponse;

public interface AuthService {
    
    //회원가입
    AuthResponse signup(SignupRequest request);
    
    // 로그인
    AuthResponse login(LoginRequest request);
}

