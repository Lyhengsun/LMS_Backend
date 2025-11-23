package com.norton.lms_backend.controller;

import com.norton.lms_backend.model.dto.request.AppUserRequest;
import com.norton.lms_backend.model.dto.request.UpdateProfileRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.norton.lms_backend.model.dto.response.ApiResponse;
import com.norton.lms_backend.model.dto.response.AppUserResponse;
import com.norton.lms_backend.service.AppUserService;
import com.norton.lms_backend.utils.ResponseUtils;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/app-users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Profile Management")
public class AppUserController {
    private final AppUserService appUserService;

    @GetMapping
    public ResponseEntity<ApiResponse<AppUserResponse>> getCurrentUser() {
        return ResponseUtils.createResponse("Fetch current user successfully", appUserService.getCurrentUserInfo());
    }

    @Operation(summary = "Set bakong account id for current user")
    @PostMapping("/bakong-account/{bakongAccountId}")
    public ResponseEntity<ApiResponse<AppUserResponse>> setBakongAccountId(@PathVariable String bakongAccountId) {
        return ResponseUtils.createResponse("Set bakongId successfully", appUserService.setBakongAccountId(bakongAccountId));
    }

    @Operation(summary = "For user to update some information of their profile")
    @PutMapping
    public ResponseEntity<ApiResponse<AppUserResponse>> updateProfile(@RequestBody UpdateProfileRequest request) {
        return ResponseUtils.createResponse("Update user profile successfully", appUserService.updateProfile(request));
    }
}
