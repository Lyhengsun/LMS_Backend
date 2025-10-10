package com.norton.lms_backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructorStatsResponse {
    private Integer myCourses;
    private Integer coursesPublished;
    private Integer coursesDraft;
    private Integer coursesPending;
    private Integer quizzesCreated;
    private Integer quizTotalAttempts;
    private Integer activeStudentsThisWeek;
    private Integer quizzesAttemptedToday;
}
