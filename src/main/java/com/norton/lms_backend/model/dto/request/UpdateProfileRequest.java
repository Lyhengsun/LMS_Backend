package com.norton.lms_backend.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateProfileRequest {
    private String avatarUrl;

    private String bio;

    @NotBlank(message = "Phone number is required")
    @Size(min = 9,max = 10, message = "Phone number must be at 9-10 characters")
    private String phoneNumber;
}
