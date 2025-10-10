package com.norton.lms_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.norton.lms_backend.model.entity.AppUser;
import com.norton.lms_backend.model.entity.Course;
import com.norton.lms_backend.model.entity.JoinCourse;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface JoinCourseRepository extends JpaRepository<JoinCourse, Long> {
    Optional<JoinCourse> findByStudentAndCourse(AppUser student, Course course);

    Page<JoinCourse> findByStudentAndIsCompleted(AppUser student, boolean isCompleted, Pageable pageable);

    Integer countJoinCourseByCourseId(Long courseId);

    Integer countJoinCourseByStudentId(Long studentId);

    @Query(value = "SELECT COUNT(DISTINCT jc.student_id) FROM join_courses jc " +
            "INNER JOIN courses c ON jc.course_id = c.id " +
            "WHERE c.author_id = :authorId", nativeQuery = true)
    Integer countDistinctStudentsByAuthorId(@Param("authorId") Long authorId);

    @Query("SELECT COUNT(DISTINCT jc.student.id) FROM JoinCourse jc " +
            "WHERE jc.course.author.id = :authorId " +
            "AND MONTH(jc.createdAt) = :month " +
            "AND YEAR(jc.createdAt) = :year")
    Integer countDistinctStudentsByAuthorIdAndMonthYear(@Param("authorId") Long authorId,
                                                        @Param("month") int month,
                                                        @Param("year") int year);

    @Query(value = """
            SELECT COUNT(DISTINCT cc.student_id)
            FROM complete_contents cc
            INNER JOIN course_contents cont ON cc.course_content_id = cont.id
            INNER JOIN courses c ON cont.course_id = c.id
            WHERE c.author_id = :authorId
            AND cc.created_at >= :startDate
            AND cc.created_at < CURRENT_DATE + INTERVAL '1 day'
            """, nativeQuery = true)
    Integer countDistinctStudentsByAuthorFromDate(
            @Param("authorId") Long authorId,
            @Param("startDate") LocalDate startDate
    );
}
