package com.norton.lms_backend.model.entity;

import com.norton.lms_backend.model.dto.response.QuizResultResponse;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.norton.lms_backend.model.dto.response.TakeQuizResponse;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "take_quizzes")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TakeQuiz extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user; // Assuming User entity exists

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(name = "deadline_time", nullable = false)
    private LocalDateTime deadlineTime;

    @Column(name = "is_submitted", nullable = false)
    private Boolean isSubmitted;

    @OneToMany(mappedBy = "takeQuiz")
    private List<UserAnswer> userAnswer;

    @Column(nullable = false)
    private Integer score;

    @PrePersist
    private void prePersist() {
        if (isSubmitted == null) isSubmitted = false;
        if (score == null) score = 0;
    }

    public TakeQuizResponse toResponse() {
        return TakeQuizResponse.builder()
                .id(getId())
                .user(user.toResponse())
                .quiz(quiz.toNoQuestionResponse())
                .deadlineTime(deadlineTime)
                .createdAt(getCreatedAt())
                .editedAt(getEditedAt())
                .build();
    }

    public QuizResultResponse toQuizResultResponse(int attemptNumber) {
        int maxScore = 0;
        List<Question> questions = this.quiz.getQuestions();
        if (questions != null && !questions.isEmpty()) {
            maxScore = questions.stream()
                    .mapToInt(Question::getScore)
                    .sum();
        }

        return QuizResultResponse.builder()
                .takeQuizId(this.getId())
                .quizId(this.quiz.getId())
                .quizName(this.quiz.getQuizName())
                .categoryName(this.quiz.getCategory().getName())
                .score(this.score)
                .maxScore(maxScore)
                .percentage((this.score * 100) / maxScore)
                .passed(this.score > maxScore/2)
                .attemptNumber(attemptNumber)
                .completedAt(this.getEditedAt())
                .timeSpentInMinutes((int) Duration.between(this.getEditedAt(), this.getCreatedAt()).toMinutes())
                .correctAnswers(userAnswer.stream().filter(UserAnswer::getIsCorrect).toList().size())
                .totalQuestions(this.quiz.getQuestions().size())
                .build();
    }
}
