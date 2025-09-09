package com.norton.lms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.norton.lms_backend.model.entity.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    
}
