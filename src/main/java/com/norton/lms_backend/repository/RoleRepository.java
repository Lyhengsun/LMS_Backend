package com.norton.lms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.norton.lms_backend.model.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
}
