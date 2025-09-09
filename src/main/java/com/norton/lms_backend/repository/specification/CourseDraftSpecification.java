package com.norton.lms_backend.repository.specification;

import org.springframework.data.jpa.domain.Specification;

import com.norton.lms_backend.model.entity.CourseDraft;
import com.norton.lms_backend.model.enumeration.CourseLevel;

import jakarta.persistence.criteria.JoinType;

public class CourseDraftSpecification {
    public static Specification<CourseDraft> courseDraftNameContains(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("courseName")),
                "%" + name.toLowerCase() + "%");
    }

    public static Specification<CourseDraft> hasCategoryId(Long categoryId) {
        return (root, query, criterialBuilder) -> criterialBuilder.equal(root.get("category").get("id"), categoryId);
    }

    public static Specification<CourseDraft> hasLevel(CourseLevel level) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("level"), level);
    }

    public static Specification<CourseDraft> isPublic(Boolean isPublic) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isPublic"), isPublic);
    }

    public static Specification<CourseDraft> hasAuthorId(Long authorId) {
        return (root, query, criterialBuilder) -> criterialBuilder.equal(root.get("author").get("id"), authorId);
    }

    // A general specification to always fetch contents, which can be combined with
    // others
    public static Specification<CourseDraft> fetchContents() {
        return (root, query, criteriaBuilder) -> {
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                root.fetch("contents", JoinType.LEFT);
                query.distinct(true);
            }
            return null; // No additional predicate, just for fetching
        };
    }
}
