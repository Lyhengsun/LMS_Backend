package com.norton.lms_backend.service.impl;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.norton.lms_backend.exception.BadRequestException;
import com.norton.lms_backend.exception.NotFoundException;
import com.norton.lms_backend.model.dto.request.AppUserRequest;
import com.norton.lms_backend.model.dto.response.AppUserResponse;
import com.norton.lms_backend.model.entity.AppUser;
import com.norton.lms_backend.model.entity.Role;
import com.norton.lms_backend.repository.AppUserRepository;
import com.norton.lms_backend.repository.RoleRepository;
import com.norton.lms_backend.service.AppUserService;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AppUserServiceImpl implements AppUserService {
    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    private AppUser getCurrentUser() {
        return (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser foundUser = appUserRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User with email: '" + username + "' doesn't exist"));

        return foundUser;
    }

    @Override
    public AppUserResponse create(AppUserRequest request) {
        Role foundRole = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new NotFoundException("Role with ID: " + request.getRoleId() + " doesn't exist"));

        if (foundRole.getRoleName().equals("ROLE_ADMIN"))
            throw new BadRequestException("You can't create another admin account");

        appUserRepository.findByEmail(request.getEmail()).ifPresent((user) -> {
            throw new BadRequestException("User with email: '" + user.getEmail() + "' already exist");
        });

        AppUser newUser = request.toEntity(foundRole);
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        return appUserRepository.save(newUser).toResponse();
    }

    @Override
    public AppUserResponse getCurrentUserInfo() {
        return getCurrentUser().toResponse();
    }
}
