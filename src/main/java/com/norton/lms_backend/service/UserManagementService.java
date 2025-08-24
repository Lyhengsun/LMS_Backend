package com.norton.lms_backend.service;

import org.springframework.data.domain.Sort.Direction;

import com.norton.lms_backend.model.dto.request.AppUserRequest;
import com.norton.lms_backend.model.dto.response.AppUserResponse;
import com.norton.lms_backend.model.dto.response.PagedResponse;
import com.norton.lms_backend.model.enumeration.UserProperty;

public interface UserManagementService {

    PagedResponse<AppUserResponse> getAllUser( Integer page, Integer size, Boolean isApproved, UserProperty userProperty, Direction direction);

    AppUserResponse getUserById(Long id);

    void deleteUserById(Long id);

    AppUserResponse updateUser(Long id, AppUserRequest appUserRequest);

    void updateDisableUser(Long id,Boolean isDisable);

    void updateApproveUser(Long id);
}
