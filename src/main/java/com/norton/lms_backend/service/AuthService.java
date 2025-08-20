package com.norton.lms_backend.service;

import com.norton.lms_backend.model.dto.request.AppUserRequest;
import com.norton.lms_backend.model.dto.request.AuthRequest;
import com.norton.lms_backend.model.dto.response.AppUserResponse;
import com.norton.lms_backend.model.dto.response.AuthResponse;
import jakarta.mail.MessagingException;

public interface AuthService {
    AuthResponse login(AuthRequest authRequest) throws Exception;

    AppUserResponse register(AppUserRequest appUserRequest) throws MessagingException;

    void verify(String email, String optCode);

    void resend(String email) throws MessagingException;
}
