package com.norton.lms_backend.controller;

import com.norton.lms_backend.model.dto.request.AppUserRequest;
import com.norton.lms_backend.model.dto.response.ApiResponse;
import com.norton.lms_backend.model.dto.response.AppUserResponse;
import com.norton.lms_backend.model.dto.response.PagedResponse;
import com.norton.lms_backend.model.enumeration.UserProperty;
import com.norton.lms_backend.service.UserManagementService;
import com.norton.lms_backend.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/admins/user-management")
@RequiredArgsConstructor
@RestController
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "User Management", description = "This controller is for admin to manage existing users.")
public class UserManagementController {
    private final UserManagementService userManagementService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<AppUserResponse>>> getAllUser(
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            @RequestParam(required = false) Boolean isApproved,
            @RequestParam(defaultValue = "ID") UserProperty userProperty,
            @RequestParam(defaultValue = "ASC") Direction direction) {
        return ResponseUtils.createResponse("Get all users", userManagementService.getAllUser(page, size, isApproved, userProperty, direction));
    }

    @GetMapping("{user-id}")
    public ResponseEntity<ApiResponse<AppUserResponse>> getUserById(@PathVariable("user-id") Long id) {
        return ResponseUtils.createResponse("Get user with id " + id + " successful",
                userManagementService.getUserById(id));
    }

    @DeleteMapping("{user-id}")
    public ResponseEntity<ApiResponse<Object>> deleteUserById(@PathVariable("user-id") Long id) {
        userManagementService.deleteUserById(id);
        return ResponseUtils.createResponse("Delete user with id " + id + " successful");
    }

    @PutMapping("update-user/{user-id}")
    public ResponseEntity<ApiResponse<AppUserResponse>> updateUser(@PathVariable("user-id") Long id,
            @RequestBody AppUserRequest appUserRequest) {
        return ResponseUtils.createResponse("Update user successfully",
                userManagementService.updateUser(id, appUserRequest));
    }

    @PatchMapping("update-user/{user-id}/disable")
    public ResponseEntity<ApiResponse<Object>> updateDisableUser(@PathVariable("user-id") Long id,
            @RequestParam(defaultValue = "false") @NotNull Boolean isDisable) {
        userManagementService.updateDisableUser(id, isDisable);
        return ResponseUtils.createResponse("Disable user with id " + id + "successfully");
    }

    @PatchMapping("update-user/{user-id}/approve")
    public ResponseEntity<ApiResponse<Object>> updateApproveUser(@PathVariable("user-id") Long id) {
        userManagementService.updateApproveUser(id);
        return ResponseUtils.createResponse("Approve user with id " + id + "successfully");
    }

}
