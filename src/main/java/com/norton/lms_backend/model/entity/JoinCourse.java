package com.norton.lms_backend.model.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "join_courses", uniqueConstraints = {@UniqueConstraint(columnNames = {"student_id", "course_id"})})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JoinCourse extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AppUser student;

    @ManyToOne
    @JoinColumn(name = "course_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Course course;

    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted;

    @PrePersist
    public void prePersist() {
        if (isCompleted == null) {
            isCompleted = false;
        }
    }
}
