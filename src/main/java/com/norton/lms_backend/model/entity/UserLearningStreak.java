package com.norton.lms_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_learning_steaks")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLearningStreak extends BaseEntity {
    @Column(nullable = false, name = "learning_streak_day")
    private Integer learningStreakDay;

    @Column(nullable = false, name = "last_day_learning")
    private LocalDateTime lastDayLearning;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AppUser appUser;

    @PrePersist
    public void prePersist() {
        if (learningStreakDay == null) {
            learningStreakDay = 1;
        }
        if (lastDayLearning == null) {
            lastDayLearning = LocalDateTime.now();
        }
    }
}
