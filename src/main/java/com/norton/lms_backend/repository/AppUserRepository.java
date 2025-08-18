package com.norton.lms_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.norton.lms_backend.model.entity.AppUser;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    public Optional<AppUser> findByEmail(String email);
}
