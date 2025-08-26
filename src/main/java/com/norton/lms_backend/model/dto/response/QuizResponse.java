package com.norton.lms_backend.model.dto.response;

import com.norton.lms_backend.model.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class QuizResponse {
    private Long quizId;
    private String quizName;
    private String quizDescription;
    private String quizInstruction;
    private String level;
    private Integer durationMinutes;
    private Integer maxAttempts;
    private Integer passingScore;
    private AppUserResponse author;
    private Category category;
}
