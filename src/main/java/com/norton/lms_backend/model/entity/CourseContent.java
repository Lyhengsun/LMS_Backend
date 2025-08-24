package com.norton.lms_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "course_contents")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseContent extends BaseEntity {

    @Column(name = "video_file_name", nullable = false, columnDefinition = "TEXT")
    private String videoFileName;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
}