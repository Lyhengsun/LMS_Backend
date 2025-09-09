package com.norton.lms_backend.model.entity;

import java.util.List;

import com.norton.lms_backend.model.dto.response.CourseResponse;
import com.norton.lms_backend.model.enumeration.CourseLevel;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "courses")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Course extends BaseEntity {
    @Column(name = "course_name", nullable = false, length = 50)
    private String courseName;

    @Column(name = "course_image_name", columnDefinition = "TEXT")
    private String courseImageName;

    @Column(name = "course_description", nullable = false, columnDefinition = "TEXT")
    private String courseDescription;

    @Column(name = "level", nullable = false, length = 20)
    private CourseLevel level;

    @Column(name = "max_points", nullable = false)
    private Integer maxPoints;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private AppUser author;

    @OneToMany(mappedBy = "course", cascade = CascadeType.REMOVE)
    private List<CourseContent> contents;

    @PrePersist
    private void prePersist() {
        if (isPublic == null) {
            isPublic = true;
        }
        if (isDeleted == null) {
            isDeleted = false;
        }
    }

    @OneToOne
    @JoinColumn(name = "course_draft_id", referencedColumnName = "id", nullable = true)
    private CourseDraft courseDraft;

    public CourseResponse toResponse() {
        List<CourseContent> checkedContents = List.of();

        Integer duration = 0;
        if (contents != null) {
            checkedContents = contents;
            for (CourseContent courseContent : checkedContents) {
                duration += courseContent.getDurationMinutes();
            }
        }

        return CourseResponse.builder()
                .id(this.getId())
                .courseName(this.courseName)
                .courseImageName(this.courseImageName)
                .courseDescription(this.courseDescription)
                .level(this.level)
                .maxPoints(this.maxPoints)
                .duration(duration)
                .isPublic(this.isPublic)
                .isDeleted(this.isDeleted)
                .category(this.category)
                .author(this.author.toResponse())
                .contents(checkedContents.stream().map((c) -> c.toResponse()).toList())
                .createdAt(this.getCreatedAt())
                .editedAt(this.getEditedAt())
                .build();
    }
}
