package com.likelion.demoday.service;

import com.likelion.demoday.dto.request.PaymentCompleteRequest;
import com.likelion.demoday.dto.request.TossWebhookRequest;

public interface PaymentService {

    void verifyAndCompletePayment(PaymentCompleteRequest request);
}

