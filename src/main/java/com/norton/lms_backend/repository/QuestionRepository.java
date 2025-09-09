package com.norton.lms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.norton.lms_backend.model.entity.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    void deleteAllByQuizId(Long id);
}
