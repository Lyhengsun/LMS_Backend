package com.norton.lms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.norton.lms_backend.model.entity.JoinCourse;

public interface JoinCourseRepository extends JpaRepository<JoinCourse, Long> {
    
}
