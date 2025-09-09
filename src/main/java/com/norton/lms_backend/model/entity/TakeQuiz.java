package com.norton.lms_backend.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

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

    @PrePersist
    private void prePersist() {
        if (isSubmitted == null) isSubmitted = false;
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
}
