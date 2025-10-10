package com.norton.lms_backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentSummaryStatResponse {
    private Integer enrolledCourses;
    private Integer courseProgress;
    private Integer quizzesTaken;
    private Integer quizPerformance;
}
