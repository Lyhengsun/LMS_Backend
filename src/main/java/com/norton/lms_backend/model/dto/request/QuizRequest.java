package com.norton.lms_backend.model.dto.request;

import com.norton.lms_backend.model.entity.AppUser;
import com.norton.lms_backend.model.entity.Quiz;
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
    private String level;
    private Integer durationMinutes;
    private Integer maxAttempts;
    private Integer passingScore;
    private Long categoryId;

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
