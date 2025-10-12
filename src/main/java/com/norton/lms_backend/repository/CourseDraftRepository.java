package com.norton.lms_backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.norton.lms_backend.model.entity.CourseDraft;

public interface CourseDraftRepository extends JpaRepository<CourseDraft, Long>, JpaSpecificationExecutor<CourseDraft> {
    @Query("Select c FROM CourseDraft c WHERE c.author.id = :authorId AND c.id = :courseId")
    Optional<CourseDraft> findByAuthorIdAndCourseId(Long authorId, Long courseId);

    Page<CourseDraft> findAllByAuthorId(Long authorId, Pageable pageable);

    Integer countByAuthorId(Long userId);

    Integer countByAuthorIdAndIsSubmitted(Long userId, Boolean isSubmitted);

    Integer countByAuthorIdAndIsApproved(Long userId, Boolean isApproved);

    Integer countByAuthorIdAndIsApprovedAndIsSubmitted(Long userId, Boolean isApproved, Boolean isSubmitted);

    Integer countByAuthorIdAndIsRejected(Long userId, Boolean isRejected);

    Page<CourseDraft> findAllByIsApproved(Boolean isApproved, Pageable pageable);
}
