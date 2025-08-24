package com.norton.lms_backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.norton.lms_backend.model.entity.AppUser;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    public Optional<AppUser> findByEmail(String email);

    Page<AppUser> findAllByIsApproved(Boolean isApproved, Pageable pageable);

    Page<AppUser> findAllByIsApprovedIsNull(Pageable pageable);

    @Query("SELECT au FROM AppUser au WHERE au.role.roleName != 'ROLE_ADMIN' AND au.isVerified = true")
    Page<AppUser> findAllNonAdminUser(Pageable pageable);

    @Query("SELECT au FROM AppUser au WHERE au.role.roleName != 'ROLE_ADMIN' AND au.isVerified = true AND au.isApproved = ?1")
    Page<AppUser> findAllNonAdminUserByIsApproved(Boolean isApproved, Pageable pageable);
}
