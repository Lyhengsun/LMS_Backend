package com.norton.lms_backend.service;

import com.norton.lms_backend.model.dto.request.CourseContentRequest;
import com.norton.lms_backend.model.dto.request.CourseRequest;
import com.norton.lms_backend.model.dto.response.CourseContentResponse;
import com.norton.lms_backend.model.dto.response.CourseResponse;
import com.norton.lms_backend.model.dto.response.PagedResponse;
import com.norton.lms_backend.model.enumeration.CourseProperty;
import com.norton.lms_backend.model.enumeration.CourseLevel;

import java.util.List;

import org.springframework.data.domain.Sort.Direction;

public interface CourseService {
    CourseResponse createCourse(CourseRequest course);

    CourseResponse getCourseById(Long id);

    PagedResponse<CourseResponse> getAllCourses(String name, Long categoryId, CourseLevel level,
            CourseProperty courseProperty, Direction direction,
            Integer page, Integer size);

    CourseResponse updateCourse(Long id, CourseRequest course);

    void deleteCourse(Long id);

    PagedResponse<CourseResponse> getCoursesByCategoryId(Long categoryId, Integer page, Integer size);

    PagedResponse<CourseResponse> getCoursesByAuthorId(String name, CourseProperty courseProperty, Direction direction,
            Integer page, Integer size);

    CourseContentResponse createCourseContent(CourseContentRequest request);

    List<CourseContentResponse> getCourseContentsByCourseId(Long courseId);

    CourseResponse getCourseByIdForAuthor(Long courseId);
}
