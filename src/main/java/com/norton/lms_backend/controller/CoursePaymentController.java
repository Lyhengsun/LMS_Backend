package com.norton.lms_backend.controller;

import com.norton.lms_backend.model.dto.response.*;
import com.norton.lms_backend.service.KHQRPaymentService;
import com.norton.lms_backend.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@Tag(name = "Course Payment", description = "endpoint used to manage payment of courses")
public class CoursePaymentController {
    private final KHQRPaymentService khqrPaymentService;

    @Operation(summary = "generate transaction payment of a course")
    @PostMapping("/payments/courses/{courseId}")
    ResponseEntity<ApiResponse<PaymentResponse>> generatePayment(@PathVariable Long courseId) {
        return ResponseUtils.createResponse("generate payment for a course successfully", HttpStatus.CREATED, khqrPaymentService.generateCoursePayment(courseId));
    }

    @Operation(summary = "check transaction payment status by transaction id")
    @GetMapping("/payments/{transactionId}")
    ResponseEntity<ApiResponse<PaymentStatusResponse>> checkPaymentStatus(@PathVariable UUID transactionId) {
        return ResponseUtils.createResponse("checked payment status successfully", HttpStatus.OK, khqrPaymentService.checkPaymentStatusByTransactionId(transactionId));
    }

    @Operation(summary = "Fetch payment by role")
    @GetMapping("/payments")
    ResponseEntity<ApiResponse<PagedResponse<CoursePaymentResponse>>> fetchPaymentByRole(
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        return ResponseUtils.createResponse("Fetch payment by user role successfully", HttpStatus.OK, khqrPaymentService.fetchPaymentByRole(page, size));
    }
}
