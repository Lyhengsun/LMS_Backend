package com.norton.lms_backend.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.norton.lms_backend.model.dto.response.ApiResponse;

public class ResponseUtils {
    public static <T> ResponseEntity<ApiResponse<T>> createResponse(String message, HttpStatus httpStatus,
            T payload) {
        ApiResponse<T> response = ApiResponse.<T>builder()
                .payload(payload)
                .message(message)
                .status(httpStatus)
                .code(httpStatus.value())
                .build();
        return ResponseEntity.status(httpStatus).body(response);
    }
}
