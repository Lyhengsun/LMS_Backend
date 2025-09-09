package com.norton.lms_backend.model.dto.response;

import java.util.List;

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
public class QuizStudentResponse extends BaseEntityResponse {
    private String quizName;
    private String quizDescription;
    private String quizInstruction;
    private CourseLevel level;
    private Integer durationMinutes;
    private Integer maxAttempts;
    private Integer passingScore;
    private AppUserResponse author;
    private Category category;
    private List<QuestionStudentResponse> questions;
}
