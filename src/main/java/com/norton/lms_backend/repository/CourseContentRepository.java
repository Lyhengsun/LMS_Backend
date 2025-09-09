package com.norton.lms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.norton.lms_backend.model.entity.Course;
import com.norton.lms_backend.model.entity.CourseContent;
import com.norton.lms_backend.model.entity.CourseDraft;

import java.util.List;

@Repository
public interface CourseContentRepository extends JpaRepository<CourseContent, Long> {
    List<CourseContent> findByCourseOrderByCourseContentIndex(Course course);

    List<CourseContent> findByCourseDraftOrderByCourseContentIndex(CourseDraft courseDraft);

    @Modifying
    @Transactional
    void deleteAllByCourseDraftId(Long id);
}
