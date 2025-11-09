package com.norton.lms_backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusResponse {
    private String status;
    private String hash;
    private String fromAccountId;
    private String toAccountId;
    private String currency;
    private BigDecimal amount;
    private String description;
}
