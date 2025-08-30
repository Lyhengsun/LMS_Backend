package com.norton.lms_backend.model.entity;

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
@Entity
@Table(name = "complete_contents", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "student_id", "course_content_id" }) })
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CompleteContent extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false, referencedColumnName = "id")
    private AppUser student;

    @ManyToOne
    @JoinColumn(name = "course_content_id", nullable = false, referencedColumnName = "id")
    private CourseContent courseContent;
}
