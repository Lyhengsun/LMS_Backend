package com.norton.lms_backend.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import com.norton.lms_backend.model.entity.Quiz;

public class QuizSpecification {
    public static Specification<Quiz> quizNameContains(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("quizName")),
                "%" + name.toLowerCase() + "%");
    }

    public static Specification<Quiz> hasAuthorId(Long authorId) {
        return (root, query, criterialBuilder) -> criterialBuilder.equal(root.get("author").get("id"), authorId);
    }
}
