package com.likelion.demoday.controller;

import com.likelion.demoday.dto.request.LoginRequest;
import com.likelion.demoday.dto.request.SignupRequest;
import com.likelion.demoday.dto.response.AuthResponse;
import com.likelion.demoday.global.response.BaseResponse;
import com.likelion.demoday.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<AuthResponse>> signup(@Valid @RequestBody SignupRequest request) {
        AuthResponse response = authService.signup(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success("회원가입 성공", response));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<BaseResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);

        return ResponseEntity.ok(BaseResponse.success("로그인 성공", response));
    }
}