package com.norton.lms_backend.service;

import com.norton.lms_backend.model.dto.response.*;

import java.util.UUID;

public interface KHQRPaymentService {
    PaymentResponse generateCoursePayment(Long courseId);

    PaymentStatusResponse checkPaymentStatusByTransactionId(UUID transactionId);

    BakongAccountResponse verifyBakongAccountId(String bakongAccountId);

    PagedResponse<CoursePaymentResponse> fetchPaymentByRole(Integer page, Integer size);
}
