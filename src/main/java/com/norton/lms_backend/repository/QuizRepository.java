package com.norton.lms_backend.repository;

import com.norton.lms_backend.model.entity.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long>, JpaSpecificationExecutor<Quiz> {
    Page<Quiz> findAllByAuthorId(Long id, Pageable pageable);
    Quiz findByAuthorIdAndId(Long authorId, Long quizId);

    Integer countByAuthorId(Long authorId);
}
