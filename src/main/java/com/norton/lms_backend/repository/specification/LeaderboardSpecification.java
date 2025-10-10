package com.norton.lms_backend.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import com.norton.lms_backend.model.entity.Leaderboard;

import jakarta.persistence.criteria.Expression;

public class LeaderboardSpecification {
    public static Specification<Leaderboard> studentNameContains(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
                criteriaBuilder.lower(root.get("student").get("fullName")),
                "%" + name.toLowerCase() + "%");
    }

    public static Specification<Leaderboard> orderByTotalPointsDesc() {
        return (root, query, criteriaBuilder) -> {
            
            // Build the sum expression
            Expression<Integer> totalPoints = criteriaBuilder.sum(
                    criteriaBuilder.sum(root.get("quizPoints"), root.get("coursePoints")),
                    root.get("assignmentPoints"));

            // Apply ordering
            query.orderBy(criteriaBuilder.desc(totalPoints));

            // No filtering (return "always true" predicate)
            return criteriaBuilder.conjunction();
        };
    }
}
