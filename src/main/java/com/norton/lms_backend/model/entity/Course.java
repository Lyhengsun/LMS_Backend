package com.norton.lms_backend.model.entity;

import java.math.BigDecimal;
import java.util.List;

import com.norton.lms_backend.model.dto.response.CourseNoContentResponse;
import com.norton.lms_backend.model.dto.response.CourseResponse;
import com.norton.lms_backend.model.enumeration.CourseAvailability;
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

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(nullable = false, columnDefinition = "numeric(38,2) DEFAULT 0")
    private BigDecimal price;

    @Column(name = "course_availability", columnDefinition = "smallint DEFAULT 0", nullable = false)
    private CourseAvailability courseAvailability;

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
        Integer maxPoints = 0;
        if (contents != null) {
            checkedContents = contents;
            for (CourseContent courseContent : checkedContents) {
                duration += courseContent.getDurationMinutes();
                maxPoints += courseContent.getPoints();
            }
        }

        CourseResponse response = CourseResponse.builder()
                .id(this.getId())
                .courseName(this.courseName)
                .courseImageName(this.courseImageName)
                .courseDescription(this.courseDescription)
                .level(this.level)
                .maxPoints(maxPoints)
                .duration(duration)
                .isPublic(this.isPublic)
                .isDeleted(this.isDeleted)
                .category(this.category)
                .courseAvailability(this.courseAvailability)
                .price(this.price.doubleValue())
                .author(this.author.toResponse())
                .contents(checkedContents.stream().map((c) -> c.toResponse()).toList())
                .createdAt(this.getCreatedAt())
                .editedAt(this.getEditedAt())
                .build();

        if (this.courseAvailability == CourseAvailability.FREE) {
            response.setIsAccessible(true);
        }
        return response;
    }

    public CourseNoContentResponse toNoContentResponse() {
        List<CourseContent> checkedContents = List.of();

        Integer duration = 0;
        Integer maxPoints = 0;
        if (contents != null) {
            checkedContents = contents;
            for (CourseContent courseContent : checkedContents) {
                duration += courseContent.getDurationMinutes();
                maxPoints += courseContent.getPoints();
            }
        }

        CourseNoContentResponse response = CourseNoContentResponse.builder()
                .id(this.getId())
                .courseName(this.courseName)
                .courseImageName(this.courseImageName)
                .courseDescription(this.courseDescription)
                .level(this.level)
                .duration(duration)
                .isPublic(this.isPublic)
                .maxPoints(maxPoints)
                .isDeleted(this.isDeleted)
                .category(this.category)
                .author(this.author.toResponse())
                .contentCount(checkedContents.size())
                .courseAvailability(this.courseAvailability)
                .price(this.price.doubleValue())
                .createdAt(this.getCreatedAt())
                .editedAt(this.getEditedAt())
                .build();

        if (this.courseAvailability == CourseAvailability.FREE) {
            response.setIsAccessible(true);
        }
        return response;
    }
}
