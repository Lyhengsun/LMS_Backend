package com.norton.lms_backend.service;

import com.norton.lms_backend.model.dto.request.AppUserRequest;
import com.norton.lms_backend.model.dto.response.AppUserResponse;
import com.norton.lms_backend.model.dto.response.PagedResponse;
import jakarta.validation.constraints.Positive;

public interface UserManagementService {

    PagedResponse<AppUserResponse> getAllUser( Integer page, Integer size, Boolean isApproved);

    AppUserResponse getUserById(Long id);

    void deleteUserById(Long id);

    AppUserResponse updateUser(Long id, AppUserRequest appUserRequest);

    void updateDisableUser(Long id,Boolean isDisable);

    void updateApproveUser(Long id);
}
