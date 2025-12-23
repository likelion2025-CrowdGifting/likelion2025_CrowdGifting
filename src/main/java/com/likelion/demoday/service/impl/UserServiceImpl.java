package com.likelion.demoday.service.impl;

import com.likelion.demoday.domain.entity.User;
import com.likelion.demoday.domain.repository.UserRepository;
import com.likelion.demoday.dto.request.UpdateBankAccountRequest;
import com.likelion.demoday.dto.request.UpdateNicknameRequest;
import com.likelion.demoday.dto.response.UserResponse;
import com.likelion.demoday.global.exception.BusinessException;
import com.likelion.demoday.global.exception.ErrorCode;
import com.likelion.demoday.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    
    @Override
    @Transactional(readOnly = true)
    public UserResponse getMyInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .bank(user.getBank())
                .accountNumber(user.getAccountNumber())
                .createdAt(user.getCreatedAt())
                .balance(user.getBalance())
                .build();
    }
    
    @Override
    @Transactional
    public UserResponse updateNickname(Long userId, UpdateNicknameRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        // 닉네임 중복 검증 (본인 닉네임 제외)
        if (!user.getNickname().equals(request.getNickname()) 
                && userRepository.existsByNickname(request.getNickname())) {
            throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
        }
        
        user.updateNickname(request.getNickname());
        user = userRepository.save(user);
        
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .bank(user.getBank())
                .accountNumber(user.getAccountNumber())
                .createdAt(user.getCreatedAt())
                .build();
    }
    
    @Override
    @Transactional
    public UserResponse updateBankAccount(Long userId, UpdateBankAccountRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        user.updateBankAccount(request.getBank(), request.getAccountNumber());
        user = userRepository.save(user);
        
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .bank(user.getBank())
                .accountNumber(user.getAccountNumber())
                .createdAt(user.getCreatedAt())
                .build();
    }
}

