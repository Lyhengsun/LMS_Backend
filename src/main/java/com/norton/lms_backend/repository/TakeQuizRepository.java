package com.norton.lms_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.norton.lms_backend.model.entity.TakeQuiz;
import com.norton.lms_backend.model.entity.AppUser;
import com.norton.lms_backend.model.entity.Quiz;


public interface TakeQuizRepository extends JpaRepository<TakeQuiz, Long> {
    List<TakeQuiz> findByQuizAndUser(Quiz quiz, AppUser user);
    List<TakeQuiz> findByQuizAndUserAndIsSubmitted(Quiz quiz, AppUser user, Boolean isSubmitted);
}
