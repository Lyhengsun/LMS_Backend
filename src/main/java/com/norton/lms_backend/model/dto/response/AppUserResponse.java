package com.norton.lms_backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AppUserResponse extends BaseEntityResponse {
    private String fullName;
    private String email;
    private Boolean isVerified;
    private Boolean isDisabled;
    private Boolean isApproved;
    private String avatarUrl;
    private String bio;
    private String role;
}
