package com.norton.lms_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.norton.lms_backend.model.dto.response.ApiResponse;
import com.norton.lms_backend.model.dto.response.AppUserResponse;
import com.norton.lms_backend.service.AppUserService;
import com.norton.lms_backend.utils.ResponseUtils;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/app_users")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class AppUserController {
    private final AppUserService appUserService;

    @GetMapping
    public ResponseEntity<ApiResponse<AppUserResponse>> getCurrentUser() {
        return ResponseUtils.createResponse("Fetch current user successfully", appUserService.getCurrentUserInfo());
    }
}
