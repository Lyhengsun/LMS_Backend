package com.norton.lms_backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.norton.lms_backend.exception.InvalidException;
import com.norton.lms_backend.jwt.JwtService;
import com.norton.lms_backend.model.dto.request.AppUserRequest;
import com.norton.lms_backend.model.dto.request.AuthRequest;
import com.norton.lms_backend.model.dto.response.ApiResponse;
import com.norton.lms_backend.model.dto.response.AppUserResponse;
import com.norton.lms_backend.model.dto.response.AuthResponse;
import com.norton.lms_backend.service.AppUserService;
import com.norton.lms_backend.utils.ResponseUtils;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AppUserService appUserService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    private void authenticate(String email, String password) throws Exception {
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (DisabledException e) {
            throw new RuntimeException("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new InvalidException(
                    "Invalid username, email, or password. Please check your credentials and try again.");
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) throws Exception {
        final UserDetails userDetails = appUserService.loadUserByUsername(request.getEmail());
        authenticate(userDetails.getUsername(), request.getPassword());

        final String token = jwtService.generateToken(userDetails);
        AuthResponse authResponse = new AuthResponse(token);

        return ResponseUtils.createResponse("Login Successfully", HttpStatus.OK, authResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AppUserResponse>> registerNewUser(
            @RequestBody AppUserRequest request) {
        AppUserResponse response = appUserService.create(request);
        // return responseEntity(true, "created user successfully", HttpStatus.CREATED,
        // response);
        return ResponseUtils.createResponse("Register new user successsfully", HttpStatus.CREATED, response);
    }
}
