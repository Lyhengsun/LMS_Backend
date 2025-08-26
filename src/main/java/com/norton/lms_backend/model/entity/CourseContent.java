package com.norton.lms_backend.model.entity;

import com.norton.lms_backend.model.dto.response.CourseContentResponse;

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

    @Column(name = "course_content_name", nullable = false)
    private String courseContentName;

    @Column(name = "course_content_index", nullable = false)
    private Integer courseContentIndex;

    @Column(name = "video_file_name", nullable = false, columnDefinition = "TEXT")
    private String videoFileName;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    public CourseContentResponse toResponse() {
        return CourseContentResponse.builder().id(getId()).courseContentName(courseContentName)
                .courseContentIndex(courseContentIndex).videoFileName(videoFileName).durationMinutes(durationMinutes)
                .createdAt(getCreatedAt()).editedAt(getEditedAt()).build();
    }
}