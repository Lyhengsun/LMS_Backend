package com.norton.lms_backend.service.impl;

import com.norton.lms_backend.exception.BadRequestException;
import com.norton.lms_backend.exception.NotFoundException;
import com.norton.lms_backend.jwt.JwtService;
import com.norton.lms_backend.model.dto.request.AppUserRequest;
import com.norton.lms_backend.model.dto.request.AuthRequest;
import com.norton.lms_backend.model.dto.response.AppUserResponse;
import com.norton.lms_backend.model.dto.response.AuthResponse;
import com.norton.lms_backend.model.entity.AppUser;
import com.norton.lms_backend.model.entity.Role;
import com.norton.lms_backend.repository.AppUserRepository;
import com.norton.lms_backend.repository.RoleRepository;
import com.norton.lms_backend.service.AppUserService;
import com.norton.lms_backend.service.AuthService;
import com.norton.lms_backend.service.EmailSenderService;
import com.norton.lms_backend.utils.RandomOtp;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@RequiredArgsConstructor
@Service
@Transactional
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final AppUserRepository appUserRepository;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final EmailSenderService emailSenderService;
    private final AppUserService appUserService;

    private void authenticate(String email, String password) {
        try {
            AppUser appUser = appUserRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("Invalid email"));

            if (!passwordEncoder.matches(password, appUser.getPassword())) {
                throw new NotFoundException("Invalid Password");
            }
            if (!appUser.getIsVerified()) {
                throw new BadRequestException("Your account is not verified");
            }
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

        } catch (DisabledException e) {
            throw new BadRequestException("USER_DISABLED" + e.getMessage());
        } catch (BadCredentialsException e) {
            throw new BadRequestException("INVALID_CREDENTIALS" + e.getMessage());
        }
    }

    @Override
    public AuthResponse login(AuthRequest authRequest) throws Exception {
        AppUser appUser = appUserRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new BadRequestException("User is not registered"));
        if (!appUser.getIsVerified())
            throw new BadRequestException("User needs to verify before login");

        authenticate(authRequest.getEmail(), authRequest.getPassword());
        final UserDetails userDetails = appUserService.loadUserByUsername(authRequest.getEmail());
        final String token = jwtService.generateToken(userDetails);
        return new AuthResponse(token);
    }

    @Override
    public AppUserResponse register(AppUserRequest appUserRequest) throws MessagingException {
        AppUser foundUser = null;
        var optionalUser = appUserRepository.findByEmail(appUserRequest.getEmail());
        if (optionalUser.isPresent()) {
            foundUser = optionalUser.get();
        }

        if (foundUser != null && foundUser.getIsVerified()) {
            throw new BadRequestException("User already exist");
        }

        Role role = roleRepository.findById(appUserRequest.getRoleId())
                .orElseThrow(() -> new BadRequestException("Role doesn't exist"));

        if (role.getRoleName().equals("ROLE_ADMIN")) {
            throw new BadRequestException("You can't register as an admin");
        }

        AppUser appUser = appUserRequest.toEntity(role);

        appUser.setPassword(passwordEncoder.encode(appUserRequest.getPassword()));

        String otp = new RandomOtp().generateOtp();

        while (redisTemplate.opsForValue().get(otp) != null) {
            otp = new RandomOtp().generateOtp();
        }

        emailSenderService.sendEmail(appUserRequest.getEmail(), otp);
        redisTemplate.opsForValue().set(appUser.getEmail(), otp, Duration.ofMinutes(2));

        if (foundUser != null) {
            appUserRepository.deleteById(foundUser.getId());
            appUserRepository.flush();
        }
        return appUserRepository.save(appUser).toResponse();
    }

    @Override
    public void verify(String email, String otpCode) {
        AppUser appUser = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User is not registered"));
        if (appUser.getIsVerified())
            throw new BadRequestException("User already verified");

        String storedOTP = redisTemplate.opsForValue().get(email);
        if (storedOTP == null)
            throw new BadRequestException("OTP already expired");
        if (!storedOTP.equals(otpCode))
            throw new BadRequestException("OTP code doesn't match");

        redisTemplate.delete(otpCode);
        appUser.setIsVerified(true);
        appUserRepository.save(appUser);
    }

    @SneakyThrows
    @Override
    public void resend(String email) {
        AppUser appUser = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new BadRequestException("User is not registered"));
        if (appUser.getIsVerified())
            throw new BadRequestException("User already verified");
        String otp = new RandomOtp().generateOtp();

        while (redisTemplate.opsForValue().get(otp) != null) {
            otp = new RandomOtp().generateOtp();
        }

        emailSenderService.sendEmail(appUser.getEmail(), otp);
        redisTemplate.opsForValue().set(appUser.getEmail(), otp, Duration.ofMinutes(5));
    }
}
