package com.norton.lms_backend.controller;

import com.norton.lms_backend.model.dto.request.CourseContentRequest;
import com.norton.lms_backend.model.dto.request.CourseRequest;
import com.norton.lms_backend.model.dto.response.ApiResponse;
import com.norton.lms_backend.model.dto.response.CourseContentResponse;
import com.norton.lms_backend.model.dto.response.CourseResponse;
import com.norton.lms_backend.model.dto.response.PagedResponse;
import com.norton.lms_backend.model.enumeration.CourseProperty;
import com.norton.lms_backend.model.enumeration.CourseLevel;
import com.norton.lms_backend.service.CourseService;
import com.norton.lms_backend.utils.ResponseUtils;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Course Management", description = "This controller is for admin to manage course")
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/course")
    public ResponseEntity<ApiResponse<PagedResponse<CourseResponse>>> getAllCourse(
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) CourseLevel level,
            @RequestParam(defaultValue = "CREATED_AT") CourseProperty courseProperty,
            @RequestParam(defaultValue = "ASC") Direction direction) {
        return ResponseUtils.createResponse("Get all courses successfully",
                courseService.getAllCourses(name, categoryId, level, courseProperty, direction, page, size));
    }

    @GetMapping("/instructors/course")
    public ResponseEntity<ApiResponse<PagedResponse<CourseResponse>>> getCourseByAuthorId(
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "CREATED_AT") CourseProperty courseProperty,
            @RequestParam(defaultValue = "ASC") Direction direction) {
        return ResponseUtils.createResponse("Get all courses by author id successfully",
                courseService.getCoursesByAuthorId(name, courseProperty, direction, page, size));
    }

    @GetMapping("/course/{course-id}")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseById(@PathVariable("course-id") Long id) {
        return ResponseUtils.createResponse("Get course by id successfully", courseService.getCourseById(id));
    }

    @PostMapping("/instructors/course")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@RequestBody CourseRequest request) {
        return ResponseUtils.createResponse("Create course successfully", courseService.createCourse(request));
    }

    @PostMapping("/instructors/course/course-contents")
    public ResponseEntity<ApiResponse<CourseContentResponse>> createCourseContent(
            @RequestBody CourseContentRequest request) {
        return ResponseUtils.createResponse("Create a course content successfully", HttpStatus.CREATED,
                courseService.createCourseContent(request));
    }

    @GetMapping("/course-contents/{courseId}")
    public ResponseEntity<ApiResponse<List<CourseContentResponse>>> getCourseContentByCourseId(
            @PathVariable Long courseId) {
        List<CourseContentResponse> response = courseService.getCourseContentsByCourseId(courseId);
        return ResponseUtils.createResponse("Fetch course content by course Id successfully", response);
    }

}
