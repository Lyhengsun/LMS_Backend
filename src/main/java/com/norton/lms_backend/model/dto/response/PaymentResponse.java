package com.norton.lms_backend.model.dto.response;

import com.norton.lms_backend.model.enumeration.PaymentStatus;
import lombok.*;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentResponse {
    private String transactionId;
    private String khqrString;
    private String md5Hash;
    private BigDecimal amount;
    private String currency;
    private PaymentStatus status;
    private String merchantName;
    private String billNumber;
    private LocalDateTime createdAt;
}
