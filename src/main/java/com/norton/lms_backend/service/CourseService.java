package com.norton.lms_backend.service;

import com.norton.lms_backend.model.dto.request.CourseContentRequest;
import com.norton.lms_backend.model.dto.request.CourseRequest;
import com.norton.lms_backend.model.dto.response.CourseContentResponse;
import com.norton.lms_backend.model.dto.response.CourseDraftResponse;
import com.norton.lms_backend.model.dto.response.CourseProgressResponse;
import com.norton.lms_backend.model.dto.response.CourseResponse;
import com.norton.lms_backend.model.dto.response.PagedResponse;
import com.norton.lms_backend.model.enumeration.CourseProperty;

import jakarta.validation.constraints.Positive;

import com.norton.lms_backend.model.enumeration.CourseLevel;

import java.util.List;

import org.springframework.data.domain.Sort.Direction;

public interface CourseService {
    CourseDraftResponse createCourse(CourseRequest course);

    CourseResponse getCourseById(Long id);

    PagedResponse<CourseResponse> getAllCourses(String name, Long categoryId, CourseLevel level,
            CourseProperty courseProperty, Direction direction,
            Integer page, Integer size);

    CourseResponse updateCourse(Long id, CourseRequest course);

    void deleteCourse(Long id);

    PagedResponse<CourseResponse> getCoursesByCategoryId(Long categoryId, Integer page, Integer size);

    PagedResponse<CourseDraftResponse> getCoursesByAuthorId(String name, CourseProperty courseProperty,
            Direction direction,
            Integer page, Integer size);

    CourseContentResponse createCourseContent(CourseContentRequest request);

    List<CourseContentResponse> getCourseContentsByCourseId(Long courseId);

    CourseDraftResponse getCourseByIdForAuthor(Long courseId);

    CourseResponse approveCourseById(Long courseId);

    PagedResponse<CourseDraftResponse> getCourseForAdmin(Integer page, Integer size, String name, Boolean isApproved, Boolean isRejected);

    CourseResponse joinCourse(Long courseId);

    CourseContentResponse completeCourseContent(Long courseContentId);

    CourseProgressResponse getCourseProgressByCourseId(Long courseId);

    void deleteCourseContentById(Long courseContentId);

    CourseDraftResponse getCourseForAdminById(Long courseDraftId);

    CourseDraftResponse submitCourseDraft(Long courseDraftId);
}
