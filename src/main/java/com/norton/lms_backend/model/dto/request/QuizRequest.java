package com.norton.lms_backend.model.dto.request;

import java.util.List;

import com.norton.lms_backend.model.entity.Question;
import com.norton.lms_backend.model.entity.Quiz;
import com.norton.lms_backend.model.enumeration.CourseLevel;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizRequest {
    private String quizName;
    private String quizDescription;
    private String quizInstruction;
    private CourseLevel level;
    private Integer durationMinutes;
    private Integer maxAttempts;
    private Integer passingScore;
    private Long categoryId;
    private List<QuestionRequest> questions;

    public Quiz toEntity() {
        return Quiz.builder()
                .quizName(quizName)
                .quizDescription(quizDescription)
                .quizInstruction(quizInstruction)
                .level(level)
                .durationMinutes(durationMinutes)
                .maxAttempts(maxAttempts)
                .passingScore(passingScore)
                .build();
    }
}
