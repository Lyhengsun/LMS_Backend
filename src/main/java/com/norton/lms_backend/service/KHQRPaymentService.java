package com.norton.lms_backend.service;

import com.norton.lms_backend.model.dto.response.PaymentResponse;
import com.norton.lms_backend.model.dto.response.PaymentStatusResponse;

import java.util.UUID;

public interface KHQRPaymentService {
    PaymentResponse generateCoursePayment(Long courseId);

    PaymentStatusResponse checkPaymentStatusByTransactionId(UUID transactionId);
}
