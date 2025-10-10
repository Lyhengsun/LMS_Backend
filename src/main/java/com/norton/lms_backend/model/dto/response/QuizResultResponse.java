package com.norton.lms_backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QuizResultResponse {
    private Long takeQuizId;
    private Long quizId;
    private String quizName;
    private String categoryName;
    private Integer score;
    private Integer maxScore;
    private Integer percentage;
    private Boolean passed;
    private Integer attemptNumber;
    private LocalDateTime completedAt;
    private Integer timeSpentInMinutes;
    private Integer correctAnswers;
    private Integer totalQuestions;
}
