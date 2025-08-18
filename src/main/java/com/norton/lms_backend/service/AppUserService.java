package com.norton.lms_backend.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.norton.lms_backend.model.dto.request.AppUserRequest;
import com.norton.lms_backend.model.dto.response.AppUserResponse;

public interface AppUserService extends UserDetailsService {

    AppUserResponse create(AppUserRequest request);
    
}
