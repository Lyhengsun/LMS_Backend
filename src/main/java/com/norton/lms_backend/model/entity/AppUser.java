package com.norton.lms_backend.model.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.norton.lms_backend.model.dto.response.AppUserResponse;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "app_users")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppUser extends BaseEntity implements UserDetails {
    @Column(name = "full_name", nullable = false, length = 50)
    private String fullName;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String password;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved;

    @Column(name = "is_disabled", nullable = false)
    private Boolean isDisabled;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "phone_number", length = 10, nullable = true)
    private String phoneNumber;

    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id")
    private Role role;

    @PrePersist
    private void prePersist() {
        if (isVerified == null) {
            isVerified = false;
        }
        if (isApproved == null) {
            isApproved = false;
        }
        if (isDisabled == null) {
            isDisabled = false;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role.getRoleName()));
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public AppUserResponse toResponse() {
        return AppUserResponse.builder().id(getId()).createdAt(getCreatedAt()).editedAt(getEditedAt())
                .fullName(fullName).email(email).isVerified(isVerified).avatarUrl(avatarUrl).bio(bio)
                .role(role.getRoleName()).phoneNumber(phoneNumber).isApproved(isApproved).isDisabled(isDisabled)
                .isVerified(isVerified).build();
    }
}
