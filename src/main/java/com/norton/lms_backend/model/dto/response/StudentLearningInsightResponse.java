package com.norton.lms_backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentLearningInsightResponse {
    private Integer lessonsThisWeek;
    private Integer quizImprovement;
    private Integer streakDays;
}
