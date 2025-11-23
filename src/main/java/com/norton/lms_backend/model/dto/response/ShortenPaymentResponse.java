package com.norton.lms_backend.model.dto.response;

import com.norton.lms_backend.model.enumeration.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortenPaymentResponse {
    private String transactionId;
    private BigDecimal amount;
    private String currency;
    private String md5Hash;
    private PaymentStatus status;
    private String merchantName;
    private String billNumber;
}
