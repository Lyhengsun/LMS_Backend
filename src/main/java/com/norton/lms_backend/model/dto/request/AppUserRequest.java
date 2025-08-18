package com.norton.lms_backend.model.dto.request;

import com.norton.lms_backend.model.entity.AppUser;
import com.norton.lms_backend.model.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppUserRequest {
    private String fullName;
    private String email;
    private String password;
    private String avatarUrl;
    private String bio;
    private Long roleId;

    public AppUser toEntity(Role role) {
        return AppUser.builder()
                .fullName(fullName)
                .email(email)
                .password(password)
                .avatarUrl(avatarUrl)
                .bio(bio)
                .role(role)
                .build();
    }
}
