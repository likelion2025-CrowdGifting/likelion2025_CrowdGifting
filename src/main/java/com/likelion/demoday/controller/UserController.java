package com.likelion.demoday.controller;

import com.likelion.demoday.dto.request.UpdateBankAccountRequest;
import com.likelion.demoday.dto.request.UpdateNicknameRequest;
import com.likelion.demoday.dto.response.UserResponse;
import com.likelion.demoday.global.response.BaseResponse;
import com.likelion.demoday.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<BaseResponse<UserResponse>> getMyInfo(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        UserResponse response = userService.getMyInfo(userId);

        return ResponseEntity.ok(BaseResponse.success(response));
    }

    // 닉네임 수정
    @PatchMapping("/me/nickname")
    public ResponseEntity<BaseResponse<UserResponse>> updateNickname(
            Authentication authentication,
            @Valid @RequestBody UpdateNicknameRequest request) {
        Long userId = Long.parseLong(authentication.getName());
        UserResponse response = userService.updateNickname(userId, request);

        return ResponseEntity.ok(BaseResponse.success(response));
    }

    // 계좌 정보 수정
    @PatchMapping("/me/bank-account")
    public ResponseEntity<BaseResponse<UserResponse>> updateBankAccount(
            Authentication authentication,
            @Valid @RequestBody UpdateBankAccountRequest request) {
        Long userId = Long.parseLong(authentication.getName());
        UserResponse response = userService.updateBankAccount(userId, request);

        return ResponseEntity.ok(BaseResponse.success(response));
    }
}