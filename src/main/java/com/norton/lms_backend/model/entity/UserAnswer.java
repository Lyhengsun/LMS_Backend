package com.norton.lms_backend.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_answers", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "take_quiz_id", "question_id"}))
@Builder
public class UserAnswer extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "take_quiz_id", nullable = false)
    private TakeQuiz takeQuiz;

    @ManyToOne
    @JoinColumn(name = "answer_id", nullable = true)
    private Answer answer;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @Column(name = "content", nullable = true)
    private String content;
}
