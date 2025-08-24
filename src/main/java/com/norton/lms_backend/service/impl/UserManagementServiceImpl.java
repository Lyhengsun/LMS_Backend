package com.norton.lms_backend.service.impl;

import com.norton.lms_backend.exception.NotFoundException;
import com.norton.lms_backend.model.dto.request.AppUserRequest;
import com.norton.lms_backend.model.dto.response.AppUserResponse;
import com.norton.lms_backend.model.dto.response.PagedResponse;
import com.norton.lms_backend.model.dto.response.PaginationInfo;
import com.norton.lms_backend.model.entity.AppUser;
import com.norton.lms_backend.model.entity.Role;
import com.norton.lms_backend.model.enumeration.UserProperty;
import com.norton.lms_backend.repository.AppUserRepository;
import com.norton.lms_backend.repository.RoleRepository;
import com.norton.lms_backend.service.UserManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserManagementServiceImpl implements UserManagementService {
    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;

    public AppUser findUserById(Long id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " does not exist"));
    }

    @Override
    public AppUserResponse getUserById(Long id) {
        return appUserRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + " does not exist")).toResponse();
    }

    @Override
    public void deleteUserById(Long id) {
        AppUser appUser = findUserById(id);
        appUserRepository.delete(appUser);
    }

    @Override
    public AppUserResponse updateUser(Long id, AppUserRequest appUserRequest) {
        AppUser appUser = findUserById(id);
        Role role = roleRepository.findById(appUserRequest.getRoleId())
                .orElseThrow(() -> new NotFoundException("Role with id " + id + " does not exist"));
        appUser.setEmail(appUserRequest.getEmail());
        appUser.setBio(appUserRequest.getBio());
        appUser.setAvatarUrl(appUserRequest.getAvatarUrl());
        appUser.setFullName(appUserRequest.getFullName());
        appUser.setRole(role);
        if (appUserRequest.getPassword() != null) {
            appUser.setPassword(appUserRequest.getPassword());
        }
        return appUserRepository.save(appUser).toResponse();
    }

    @Override
    public void updateDisableUser(Long id, Boolean isDisable) {
        AppUser appUser = findUserById(id);
        appUser.setIsDisabled(isDisable);
        appUserRepository.save(appUser);
    }

    @Override
    public void updateApproveUser(Long id) {
        AppUser appUser = findUserById(id);
        appUser.setIsApproved(true);
        appUserRepository.save(appUser);
    }

    @Override
    public PagedResponse<AppUserResponse> getAllUser(Integer page, Integer size, Boolean isApproved,
            UserProperty userProperty, Direction direction) {
        Pageable pageable = PageRequest.of(
                Math.max(page - 1, 0),
                Math.max(size, 1),
                Sort.by(direction, userProperty.getValue()));
        Page<AppUser> userResponses;
        if (isApproved == null) {
            userResponses = appUserRepository.findAllNonAdminUser(pageable);
        } else {
            userResponses = appUserRepository.findAllByIsApproved(isApproved, pageable);
        }

        return PagedResponse.<AppUserResponse>builder()
                .items(userResponses.getContent().stream().map(AppUser::toResponse).toList())
                .pagination(new PaginationInfo(userResponses))
                .build();
    }

}
