package com.norton.lms_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.norton.lms_backend.model.entity.Course;
import com.norton.lms_backend.model.entity.CourseContent;
import java.util.List;

@Repository
public interface CourseContentRepository extends JpaRepository<CourseContent, Long> {
    List<CourseContent> findByCourseOrderByCourseContentIndex(Course course);
}
