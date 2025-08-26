package com.norton.lms_backend.model.dto.request;

import com.norton.lms_backend.model.entity.AppUser;
import com.norton.lms_backend.model.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.*;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppUserRequest {
    @NotBlank(message = "Full name is required")
    @Size(max = 50, message = "Full name must not exceed 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    private String avatarUrl;

    private String bio;

    @NotBlank(message = "Phone number is required")
    @Size(min = 9,max = 10, message = "Phone number must be at 9-10 characters")
    private String phoneNumber;

    @NotNull(message = "Role ID is required")
    private Long roleId;

    public AppUser toEntity(Role role) {
        return AppUser.builder()
                .fullName(fullName)
                .email(email)
                .password(password)
                .avatarUrl(avatarUrl)
                .bio(bio)
                .phoneNumber(phoneNumber)
                .role(role)
                .build();
    }
}
