package com.norton.lms_backend.controller;

import com.norton.lms_backend.model.dto.request.CourseContentRequest;
import com.norton.lms_backend.model.dto.request.CourseRequest;
import com.norton.lms_backend.model.dto.response.ApiResponse;
import com.norton.lms_backend.model.dto.response.CourseContentResponse;
import com.norton.lms_backend.model.dto.response.CourseDraftResponse;
import com.norton.lms_backend.model.dto.response.CourseProgressResponse;
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

@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Course Management", description = "This controller is for admin to manage course")
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/courses")
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

    @GetMapping("/instructors/courses")
    public ResponseEntity<ApiResponse<PagedResponse<CourseDraftResponse>>> getCourseByAuthorId(
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) CourseLevel level,
            @RequestParam(defaultValue = "CREATED_AT") CourseProperty courseProperty,
            @RequestParam(defaultValue = "ASC") Direction direction) {
        return ResponseUtils.createResponse("Get all courses by author id successfully",
                courseService.getCoursesForAuthor(name, categoryId, level, courseProperty, direction, page, size));
    }

    @GetMapping("/instructors/courses/{courseId}")
    public ResponseEntity<ApiResponse<CourseDraftResponse>> getCourseForAuthorByCourseId(@PathVariable Long courseId) {
        return ResponseUtils.createResponse("Get course with ID: " + courseId + " for author successfully",
                courseService.getCourseByIdForAuthor(courseId));
    }

    @GetMapping("/courses/{course-id}")
    public ResponseEntity<ApiResponse<CourseResponse>> getCourseById(@PathVariable("course-id") Long id) {
        return ResponseUtils.createResponse("Get course by id successfully", courseService.getCourseById(id));
    }

    @PostMapping("/instructors/courses")
    public ResponseEntity<ApiResponse<CourseDraftResponse>> createCourse(@RequestBody CourseRequest request) {
        return ResponseUtils.createResponse("Create course successfully", courseService.createCourse(request));
    }

    @PatchMapping("/instructors/courses/{courseDraftId}/submit")
    public ResponseEntity<ApiResponse<CourseDraftResponse>> submitCourseDraft(@PathVariable Long courseDraftId) {
        return ResponseUtils.createResponse("Submit course successfully", courseService.submitCourseDraft(courseDraftId));
    }

    @PostMapping("/instructors/courses/course-contents")
    public ResponseEntity<ApiResponse<CourseContentResponse>> createCourseContent(
            @RequestBody CourseContentRequest request) {
        return ResponseUtils.createResponse("Create a course content successfully", HttpStatus.CREATED,
                courseService.createCourseContent(request));
    }

    @DeleteMapping("/instructors/courses/course-contents/{courseContentId}")
    public ResponseEntity<ApiResponse<Void>> deleteCourseContentById(@PathVariable Long courseContentId) {
        courseService.deleteCourseContentById(courseContentId);
        return ResponseUtils.createResponse("Delete the course content successfully");
    }

    @GetMapping("/course-contents/{courseId}")
    public ResponseEntity<ApiResponse<List<CourseContentResponse>>> getCourseContentByCourseId(
            @PathVariable Long courseId) {
        List<CourseContentResponse> response = courseService.getCourseContentsByCourseId(courseId);
        return ResponseUtils.createResponse("Fetch course content by course Id successfully", response);
    }

    @DeleteMapping("/instructors/courses/{courseId}")
    public ResponseEntity<ApiResponse<Void>> deleteCourseById(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseUtils.createResponse("Delete Course successfully");
    }

    @DeleteMapping("/admins/courses/{courseId}")
    public ResponseEntity<ApiResponse<Void>> deleteCourseByIdForAdmin(@PathVariable Long courseId) {
        courseService.deleteCourseForAdmin(courseId);
        return ResponseUtils.createResponse("Delete Course successfully");
    }

    @PatchMapping("/admins/courses/approval/{courseId}")
    public ResponseEntity<ApiResponse<CourseResponse>> approveCourseById(@PathVariable Long courseId) {
        return ResponseUtils.createResponse("Approve with ID: " + courseId + " successfully",
                courseService.approveCourseById(courseId));
    }

    @GetMapping("/admins/courses")
    public ResponseEntity<ApiResponse<PagedResponse<CourseDraftResponse>>> getUnapprovedCourse(
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean isApproved,
            @RequestParam(required = false) Boolean isRejected) {
        return ResponseUtils.createResponse("Fetch unapproved course successfully",
                courseService.getCourseForAdmin(page, size, name, isApproved, isRejected));
    }

    @GetMapping("/admins/courses/{courseDraftId}")
    public ResponseEntity<ApiResponse<CourseDraftResponse>> getCourseForAdminById(@PathVariable Long courseDraftId) {
        return ResponseUtils.createResponse("Fetch course with ID: " + courseDraftId + " for admin successfully",
                courseService.getCourseForAdminById(courseDraftId));
    }

    @PostMapping("/students/courses/{courseId}/joining")
    public ResponseEntity<ApiResponse<CourseResponse>> joinCourse(@PathVariable Long courseId) {
        return ResponseUtils.createResponse("Join a course with ID: " + courseId + " successfully",
                courseService.joinCourse(courseId));
    }

    @PostMapping("/students/course-contents/{courseContentId}/complete")
    public ResponseEntity<ApiResponse<CourseContentResponse>> completeCourseContent(
            @PathVariable Long courseContentId) {
        return ResponseUtils.createResponse("Complete course content with ID: " + courseContentId + " successfully",
                courseService.completeCourseContent(courseContentId));
    }

    @GetMapping("/students/courses/{courseId}/progress")
    public ResponseEntity<ApiResponse<CourseProgressResponse>> getCourseProgressByCourseId(
            @PathVariable Long courseId) {
        return ResponseUtils.createResponse("fetch Course progress successfully",
                courseService.getCourseProgressByCourseId(courseId));
    }

}
