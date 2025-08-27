package com.norton.lms_backend.service;

import com.norton.lms_backend.model.dto.request.CourseContentRequest;
import com.norton.lms_backend.model.dto.request.CourseRequest;
import com.norton.lms_backend.model.dto.response.CourseContentResponse;
import com.norton.lms_backend.model.dto.response.CourseResponse;
import com.norton.lms_backend.model.dto.response.PagedResponse;
import com.norton.lms_backend.model.entity.Course;

import java.util.List;

import org.springframework.data.domain.Page;

public interface CourseService {
    CourseResponse createCourse(CourseRequest course);

    CourseResponse getCourseById(Long id);

    PagedResponse<CourseResponse> getAllCourses(Integer page, Integer size);

    CourseResponse updateCourse(Long id, CourseRequest course);

    void deleteCourse(Long id);

    PagedResponse<CourseResponse> getCoursesByCategoryId(Long categoryId, Integer page, Integer size);

    PagedResponse<CourseResponse> getCoursesByAuthorId(Integer page, Integer size);

    CourseContentResponse createCourseContent(CourseContentRequest request);

    List<CourseContentResponse> getCourseContentsByCourseId(Long courseId);
}
