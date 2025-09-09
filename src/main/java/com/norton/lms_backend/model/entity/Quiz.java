package com.norton.lms_backend.model.entity;

import com.norton.lms_backend.model.dto.response.QuestionResponse;
import com.norton.lms_backend.model.dto.response.QuestionStudentResponse;
import com.norton.lms_backend.model.dto.response.QuizNoQuestionResponse;
import com.norton.lms_backend.model.dto.response.QuizResponse;
import com.norton.lms_backend.model.dto.response.QuizStudentResponse;
import com.norton.lms_backend.model.enumeration.CourseLevel;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import jakarta.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "quizzes")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Quiz extends BaseEntity {
    @Column(name = "quiz_name", nullable = false, length = 50)
    private String quizName;

    @Column(name = "quiz_description", nullable = false, columnDefinition = "text")
    private String quizDescription;

    @Column(name = "quiz_instruction", nullable = false, columnDefinition = "text")
    private String quizInstruction;

    @Column(name = "level", nullable = false, length = 20)
    private CourseLevel level;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "max_attempts", nullable = false)
    private Integer maxAttempts;

    @Column(name = "max_score", nullable = false)
    private Integer maxScore;

    @Column(name = "passing_score", nullable = false)
    private Integer passingScore;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private AppUser author;

    @ManyToOne
    @JoinColumn(name = "quiz_category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<Question> questions;

    @PrePersist
    private void prePersist() {
        if (durationMinutes == null)
            durationMinutes = 0;
        if (maxAttempts == null)
            maxAttempts = 0;
        if (passingScore == null)
            passingScore = 0;
        if (maxScore == null)
            maxScore = 0;
    }

    public QuizResponse toResponse() {
        List<QuestionResponse> questionResponses = List.of();
        if (questions != null && questions.size() > 0) {
            questionResponses = questions.stream().map(q -> q.toResponse()).toList();
        }

        return QuizResponse.builder()
                .id(this.getId()) // from BaseEntity
                .quizName(this.quizName)
                .quizDescription(this.quizDescription)
                .quizInstruction(this.quizInstruction)
                .level(this.level)
                .durationMinutes(this.durationMinutes)
                .maxAttempts(this.maxAttempts)
                .passingScore(this.passingScore)
                .author(author.toResponse())
                .category(category)
                .questions(questionResponses)
                .createdAt(getCreatedAt())
                .editedAt(getEditedAt())
                .build();
    }

    public QuizStudentResponse toStudentResponse() {
        List<QuestionStudentResponse> questionResponses = List.of();
        if (questions != null && questions.size() > 0) {
            questionResponses = questions.stream().map(q -> q.toStudentResponse()).toList();
        }

        return QuizStudentResponse.builder()
                .id(this.getId()) // from BaseEntity
                .quizName(this.quizName)
                .quizDescription(this.quizDescription)
                .quizInstruction(this.quizInstruction)
                .level(this.level)
                .durationMinutes(this.durationMinutes)
                .maxAttempts(this.maxAttempts)
                .passingScore(this.passingScore)
                .author(author.toResponse())
                .category(category)
                .questions(questionResponses)
                .createdAt(getCreatedAt())
                .editedAt(getEditedAt())
                .build();
    }

    public QuizNoQuestionResponse toNoQuestionResponse() {
        return QuizNoQuestionResponse.builder()
                .id(this.getId()) // from BaseEntity
                .quizName(this.quizName)
                .quizDescription(this.quizDescription)
                .quizInstruction(this.quizInstruction)
                .level(this.level)
                .durationMinutes(this.durationMinutes)
                .maxAttempts(this.maxAttempts)
                .passingScore(this.passingScore)
                .author(author.toResponse())
                .category(category)
                .createdAt(getCreatedAt())
                .editedAt(getEditedAt())
                .build();
    }
}
