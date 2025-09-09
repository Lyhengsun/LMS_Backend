package com.norton.lms_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.norton.lms_backend.model.entity.UserAnswer;

import jakarta.transaction.Transactional;

import com.norton.lms_backend.model.entity.AppUser;
import com.norton.lms_backend.model.entity.Question;
import com.norton.lms_backend.model.entity.TakeQuiz;



public interface UserAnswerRepository extends JpaRepository<UserAnswer, Long> {
    List<UserAnswer> findByUserAndQuestion(AppUser user, Question question);

    UserAnswer findByUserAndQuestionAndTakeQuiz(AppUser user, Question question, TakeQuiz takeQuiz);
    
    void deleteByUserAndQuestion(AppUser user, Question question);

    @Transactional
    @Modifying
    void deleteByUserAndQuestionAndTakeQuiz(AppUser user, Question question, TakeQuiz takeQuiz);
}
