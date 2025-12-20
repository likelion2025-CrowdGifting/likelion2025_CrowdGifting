package com.likelion.demoday.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PaymentCompleteRequest {
    private String impUid;
    private String merchantUid;
}