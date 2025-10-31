package com.norton.lms_backend.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.norton.lms_backend.model.entity.AppUser;
import com.norton.lms_backend.model.entity.CompleteContent;
import com.norton.lms_backend.model.entity.Course;
import com.norton.lms_backend.model.entity.CourseContent;

@Repository
public interface CompleteContentRepository extends JpaRepository<CompleteContent, Long> {
    CompleteContent findByStudentAndCourseContent(AppUser student, CourseContent courseContent);

    List<CompleteContent> findByStudent(AppUser student);

    Integer countByStudentAndCourseContentCourse(AppUser student, Course course);

    List<CompleteContent> findByStudentAndCourseContentCourse(AppUser student, Course course);

    @Query(value = """
            SELECT COALESCE(AVG(progress_percentage), 0) as average_progress
            FROM (
                SELECT 
                    cont.course_id,
                    (COUNT(cc.id) * 100.0 / 
                        (SELECT COUNT(*)
                         FROM course_contents content
                         WHERE content.course_id = cont.course_id)) as progress_percentage
                FROM complete_contents cc
                INNER JOIN course_contents cont ON cc.course_content_id = cont.id
                WHERE cc.student_id = :studentId
                GROUP BY cont.course_id
            ) as course_progress
            """, nativeQuery = true)
    Double findAverageCourseProgressByStudentId(@Param("studentId") Long studentId);

    @Query(value = """
            SELECT COUNT(*)
            FROM complete_contents
            WHERE student_id = :studentId
            AND created_at >= :startDate
            AND created_at < CURRENT_DATE + INTERVAL '1 day'
            """, nativeQuery = true)
    Integer countCompletedContentByStudent(
            @Param("studentId") Long studentId,
            @Param("startDate") LocalDate startDate
    );

}
