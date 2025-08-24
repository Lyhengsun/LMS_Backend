package com.norton.lms_backend.controller;

import com.norton.lms_backend.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.*;

import com.norton.lms_backend.model.dto.request.AppUserRequest;
import com.norton.lms_backend.model.dto.request.AuthRequest;
import com.norton.lms_backend.model.dto.response.ApiResponse;
import com.norton.lms_backend.model.dto.response.AppUserResponse;
import com.norton.lms_backend.model.dto.response.AuthResponse;
import com.norton.lms_backend.utils.ResponseUtils;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authenticate users")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "User login")
    public ResponseEntity<ApiResponse<AuthResponse>> login( @RequestBody AuthRequest request) throws Exception {
        return ResponseUtils.createResponse("Login successfully", authService.login(request));
    }

    @SneakyThrows
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse<AppUserResponse>> register( @RequestBody AppUserRequest request){
        return ResponseUtils.createResponse("Register successfully", HttpStatus.CREATED, authService.register(request));
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify email with OTP")
    public ResponseEntity<ApiResponse<Object>> verify(@Email @RequestParam String email, @RequestParam @Positive(message = "Otp code cannot be negative or zero") String otpCode) {
        authService.verify(email, otpCode);
        return ResponseUtils.createResponse("Verified successfully!!!");
    }

    @SneakyThrows
    @PostMapping("/resend")
    @Operation(summary = "Resent verification OTP")
    public ResponseEntity<ApiResponse<Object>> resend(@Email @RequestParam String email) {
        authService.resend(email);
        return ResponseUtils.createResponse("OTP has successfully resent");
    }
}
