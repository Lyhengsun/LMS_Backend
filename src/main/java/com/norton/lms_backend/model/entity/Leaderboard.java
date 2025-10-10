package com.norton.lms_backend.model.entity;

import com.norton.lms_backend.model.dto.response.LeaderboardResponse;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "leaderboards")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Leaderboard extends BaseEntity {
    @Column(name = "quiz_points", nullable = false)
    private Integer quizPoints;

    @Column(name = "course_points", nullable = false)
    private Integer coursePoints;

    @Column(name = "assignment_points", nullable = false)
    private Integer assignmentPoints;

    @OneToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false, unique = true)
    private AppUser student;

    @PrePersist
    private void prePersist() {
        if (quizPoints == null) {
            quizPoints = 0;
        }

        if (coursePoints == null) {
            coursePoints = 0;
        }

        if (assignmentPoints == null) {
            assignmentPoints = 0;
        }
    }

    public LeaderboardResponse toResponse() {
        return LeaderboardResponse.builder()
                .id(getId())
                .quizPoints(quizPoints)
                .coursePoints(coursePoints)
                .assignmentPoints(assignmentPoints)
                .totalPoints(quizPoints + coursePoints + assignmentPoints)
                .createdAt(getCreatedAt())
                .editedAt(getEditedAt())
                .student(student.toResponse())
                .build();
    }
}
