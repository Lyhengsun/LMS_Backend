package com.norton.lms_backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BakongAccountResponse {
    private int errorCode;
    private int responseCode;
    private String responseMessage;
    private BakongAccountData data;

    public static class BakongAccountData {
        private String accountStatus; // e.g., "active", "inactive"
        private String userName;
    }
}
