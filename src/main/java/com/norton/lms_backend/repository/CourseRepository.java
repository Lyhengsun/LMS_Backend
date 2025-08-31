package com.norton.lms_backend.repository;

import com.norton.lms_backend.model.entity.Course;
import com.norton.lms_backend.model.enumeration.CourseLevel;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {
    Page<Course> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Course> findByLevel(CourseLevel level, Pageable pageable);

    Page<Course> findByCategoryIdAndLevel(Long categoryId, CourseLevel level, Pageable pageable);

    @Query("SELECT c FROM Course c JOIN FETCH c.contents WHERE c.courseName ILIKE %:name%")
    Page<Course> searchCourses(String name, Pageable pageable);

    @Query("SELECT c FROM Course c JOIN FETCH c.contents WHERE c.courseName ILIKE %:name% AND c.level = :level")
    Page<Course> searchCoursesByLevel(String name, CourseLevel level, Pageable pageable);

    @Query("SELECT c FROM Course c JOIN FETCH c.contents WHERE c.category.id = :categoryId AND c.courseName ILIKE %:name%")
    Page<Course> searchCoursesByCategoryId(Long categoryId, String name, Pageable pageable);

    @Query("SELECT c FROM Course c JOIN FETCH c.contents WHERE c.category.id = :categoryId AND c.courseName ILIKE %:name% AND level = :level")
    Page<Course> searchCoursesByCategoryIdAndLevel(Long categoryId, CourseLevel level, String name, Pageable pageable);

    @Query("Select c from Course c JOIN FETCH c.contents WHERE c.author.id = :authorId ")
    Page<Course> findByAuthorId(Long authorId, Pageable pageable);

    @Query("Select c from Course c JOIN FETCH c.contents WHERE c.author.id = :authorId AND c.courseName ILIKE %:name% ")
    Page<Course> searchByAuthorId(String name, Long authorId, Pageable pageable);

    @Query("Select c FROM Course c WHERE c.author.id = :authorId AND c.id = :courseId")
    Optional<Course> findByAuthorIdAndCourseId(Long authorId, Long courseId);
}