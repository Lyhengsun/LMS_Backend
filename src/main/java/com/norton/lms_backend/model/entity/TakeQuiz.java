package com.norton.lms_backend.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

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
}
