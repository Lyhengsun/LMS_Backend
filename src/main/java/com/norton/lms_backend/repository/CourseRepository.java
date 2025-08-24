package com.norton.lms_backend.repository;

import com.norton.lms_backend.model.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Page<Course> findCoursesByCategoryId(Long categoryId, Pageable pageable);
    Page<Course> findCoursesByAuthorId(Long authorId, Pageable pageable);
}
