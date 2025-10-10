package com.norton.lms_backend.service;

import com.norton.lms_backend.model.dto.response.*;
import jakarta.validation.constraints.Positive;

public interface DashboardService {

    StudentSummaryStatResponse getStudentSummaryStat();

    StudentLearningInsightResponse getStudentLearningInsight();

    PagedResponse<ContinueLearningResponse> getStudentContinueLearnings(Integer page, Integer size);

    PagedResponse<QuizOverviewResponse> getStudentQuizOverview(@Positive Integer page, @Positive Integer size);

    InstructorStatsResponse getInstructorSummaryStat();

    PagedResponse<InstructorDashboardCourseResponse> getInstructorCoursesForDashboard(@Positive Integer page, @Positive Integer size);
}
