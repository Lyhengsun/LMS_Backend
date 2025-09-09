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

    Page<CourseDraft> findAllByIsApproved(Boolean isApproved, Pageable pageable);
}
