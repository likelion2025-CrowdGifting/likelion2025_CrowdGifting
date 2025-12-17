package com.likelion.demoday.service;

import com.likelion.demoday.dto.response.VirtualAccountInfo;

import java.math.BigDecimal;

public interface TossPaymentService {
    
    // 가상계좌 발급
    VirtualAccountInfo createVirtualAccount(String orderId, BigDecimal amount, String customerName);
}

