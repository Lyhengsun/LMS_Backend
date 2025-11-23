package com.norton.lms_backend.controller;

import com.norton.lms_backend.model.dto.response.*;
import com.norton.lms_backend.service.DashboardService;
import com.norton.lms_backend.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/students/dashboard/summary-stat")
    ResponseEntity<ApiResponse<StudentSummaryStatResponse>> getStudentSummaryStat() {
        return ResponseUtils.createResponse("Fetch summary stats successfully", dashboardService.getStudentSummaryStat());
    }

    @GetMapping("/students/dashboard/learning-insights")
    ResponseEntity<ApiResponse<StudentLearningInsightResponse>> getStudentLearningInsights() {
        return ResponseUtils.createResponse("Fetch student learning insight successfully", dashboardService.getStudentLearningInsight());
    }

    @GetMapping("/students/dashboard/continue-learnings")
    ResponseEntity<ApiResponse<PagedResponse<ContinueLearningResponse>>> getStudentContinueLearnings(
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        return ResponseUtils.createResponse("Fetch student continue learnings successfully", dashboardService.getStudentContinueLearnings(page, size));
    }

    @GetMapping("/students/dashboard/quiz-overview")
    ResponseEntity<ApiResponse<PagedResponse<QuizOverviewResponse>>> getQuizOverview(
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        return ResponseUtils.createResponse("Fetch quiz overview for student successfully", dashboardService.getStudentQuizOverview(page, size));
    }

    @GetMapping("/instructors/dashboard/summary-stat")
    ResponseEntity<ApiResponse<InstructorStatsResponse>> getInstructorSummaryStat() {
        return ResponseUtils.createResponse("Fetch summary stats for instructor successfully", dashboardService.getInstructorSummaryStat());
    }

    @GetMapping("/instructors/dashboard/courses")
    ResponseEntity<ApiResponse<PagedResponse<InstructorDashboardCourseResponse>>> getInstructorCoursesForDashboard(
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "10") @Positive Integer size
    ) {
        return ResponseUtils.createResponse("Fetch courses for instructor dashboard successfully", dashboardService.getInstructorCoursesForDashboard(page, size));
    }

    @GetMapping("/instructors/dashboard/course-status-distribution")
    ResponseEntity<ApiResponse<CourseStatusDistributionResponse>> getCourseStatusDistribution() {
        return ResponseUtils.createResponse("Fetch course status distribution successfully", dashboardService.getCourseStatusDistribution());
    }

    @GetMapping("/instructors/dashboard/quiz-performance-distribution")
    ResponseEntity<ApiResponse<QuizPerformanceDistributionResponse>> getQuizPerformanceDistribution() {
        return ResponseUtils.createResponse("Fetch quiz performance distribution successfully", dashboardService.getQuizPerformanceDistribution());
    }

    @GetMapping("/instructors/dashboard/quiz-attempts-over-time")
    ResponseEntity<ApiResponse<QuizAttemptsOverTimeResponse>> getQuizAttemptsOverTime(
            @RequestParam(defaultValue = "30") @Positive Integer days
    ) {
        return ResponseUtils.createResponse("Fetch quiz attempts over time successfully", dashboardService.getQuizAttemptsOverTime(days));
    }

    @GetMapping("/instructors/dashboard/courses/{courseId}/student-course-progresses")
    ResponseEntity<ApiResponse<PagedResponse<StudentCourseProgressResponse>>> getStudentCourseProgressesByCourseId(
            @PathVariable Long courseId,
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            @RequestParam(required = false) String name
    ) {
        return ResponseUtils.createResponse("Fetch student course progress for a course successfully", dashboardService.getStudentCourseProgressesByCourseId(courseId, page, size, name));
    }
}
