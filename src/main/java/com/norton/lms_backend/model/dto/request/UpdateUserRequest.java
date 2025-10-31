package com.norton.lms_backend.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateUserRequest {
    @NotBlank(message = "Full name is required")
    @Size(max = 50, message = "Full name must not exceed 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    private String avatarUrl;

    private String bio;

    @NotBlank(message = "Phone number is required")
    @Size(min = 9,max = 10, message = "Phone number must be at 9-10 characters")
    private String phoneNumber;
}
