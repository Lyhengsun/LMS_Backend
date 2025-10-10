package com.norton.lms_backend.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.norton.lms_backend.model.entity.Category;
import com.norton.lms_backend.model.enumeration.CourseLevel;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuizNoQuestionResponse extends BaseEntityResponse {
    private String quizName;
    private String quizDescription;
    private String quizInstruction;
    private CourseLevel level;
    private Integer durationMinutes;
    private Integer maxAttempts;
    private Integer maxScore;
    private AppUserResponse author;
    private Category category;
    private Integer questionCount;
    private Integer attemptCount;
}
