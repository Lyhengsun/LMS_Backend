package com.norton.lms_backend.model.entity;

import java.util.List;

import com.norton.lms_backend.model.dto.response.CourseResponse;
import com.norton.lms_backend.model.enumeration.Level;
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
    private Level level;

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

    @OneToMany(mappedBy = "course")
    private List<CourseContent> contents;

    @PrePersist
    private void prePersist() {
        if (isDeleted == null) {
            isDeleted = false;
        }
    }

    public CourseResponse toResponse() {
        return CourseResponse.builder()
                .id(this.getId())
                .courseName(this.courseName)
                .courseImageName(this.courseImageName)
                .courseDescription(this.courseDescription)
                .level(this.level)
                .maxPoints(this.maxPoints)
                .isPublic(this.isPublic)
                .isDeleted(this.isDeleted)
                .category(this.category)
                .author(this.author.toResponse())
                .contents(this.contents.stream().map((c) -> c.toResponse()).toList())
                .createdAt(this.getCreatedAt())
                .editedAt(this.getEditedAt())
                .build();
    }
}
