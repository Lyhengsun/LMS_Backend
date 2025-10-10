package com.norton.lms_backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizOverviewResponse {
    private Long quizId;
    private String quizName;
    private String categoryName;
    private Integer bestScore;
    private Integer maxScore;
    private Integer attempts;
    private Integer maxAttempts;
}