package com.norton.lms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.norton.lms_backend.model.entity.Question;

import jakarta.transaction.Transactional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Transactional
    @Modifying
    void deleteAllByQuizId(Long id);
}
