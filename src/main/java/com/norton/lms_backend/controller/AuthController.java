package com.norton.lms_backend.controller;

import com.norton.lms_backend.model.dto.request.ResetRequest;
import com.norton.lms_backend.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    @PostMapping("/forgot")
    @Operation(summary = "Send otp code to email")
    public ResponseEntity<ApiResponse<Object>> verifyEmail(@Email @RequestParam String email) {
        authService.forgotPassword(email);
        return ResponseUtils.createResponse("Email verified");
    }

    @PostMapping("/forgot/verify")
    @Operation(summary = "Verify otp in forgot password")
    public ResponseEntity<ApiResponse<Object>> verifyForgot(@Email @RequestParam String email, @RequestParam String otp) {
        authService.verifyForgot(email, otp);
        return ResponseUtils.createResponse("Verify forgot password");
    }

    @PostMapping("/forgot/reset")
    @Operation(summary = "Reset password otp in forgot password")
    public ResponseEntity<ApiResponse<Object>> resetPassword(@Valid @RequestBody ResetRequest request) {
        return ResponseUtils.createResponse("successful", authService.resetPassword(request.getEmail(), request.getOtp(), request.getPassword()));
    }
}
