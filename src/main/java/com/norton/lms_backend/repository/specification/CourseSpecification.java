package com.norton.lms_backend.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import com.norton.lms_backend.model.entity.Course;
import com.norton.lms_backend.model.enumeration.CourseLevel;

import jakarta.persistence.criteria.JoinType;

public class CourseSpecification {
    public static Specification<Course> courseNameContains(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("courseName")),
                "%" + name.toLowerCase() + "%");
    }

    public static Specification<Course> hasCategoryId(Long categoryId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<Course> hasLevel(CourseLevel level) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("level"), level);
    }

    public static Specification<Course> hasAuthorId(Long authorId) {
        return (root, query, criterialBuilder) -> criterialBuilder.equal(root.get("author").get("id"), authorId);
    }

    // A general specification to always fetch contents, which can be combined with
    // others
    public static Specification<Course> fetchContents() {
        return (root, query, criteriaBuilder) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("contents", JoinType.LEFT);
                query.distinct(true);
            }
            return null; // No additional predicate, just for fetching
        };
    }
}