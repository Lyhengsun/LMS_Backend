package com.norton.lms_backend.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BakongAccountRequest {
    private String accountId; // This could also be a phone number field depending on the API
}
