package com.norton.lms_backend.model.entity;

import java.util.List;

import com.norton.lms_backend.model.dto.response.CourseDraftResponse;
import com.norton.lms_backend.model.enumeration.CourseLevel;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "course_drafts")
@Builder
public class CourseDraft extends BaseEntity {
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

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved;

    @Column(name = "is_rejected", nullable = false)
    private Boolean isRejected;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private AppUser author;

    @OneToMany(mappedBy = "courseDraft")
    private List<CourseContent> contents;

    @PrePersist
    private void prePersist() {
        if (isApproved == null) {
            isApproved = false;
        }
        if (isRejected == null) {
            isRejected = false;
        }
    }

    public Course toCourse() {
        return Course.builder()
                .courseName(this.courseName)
                .courseImageName(courseImageName)
                .courseDescription(this.courseDescription)
                .level(this.level)
                .maxPoints(this.maxPoints)
                .category(category)
                .author(author)
                .courseDraft(this)
                .build();
    }

    public CourseDraftResponse toResponse() {
        List<CourseContent> checkedContents = List.of();

        Integer duration = 0;
        if (contents != null) {
            checkedContents = contents;
            for (CourseContent courseContent : checkedContents) {
                duration += courseContent.getDurationMinutes();
            }
        }

        return CourseDraftResponse.builder()
                .id(getId())
                .courseName(courseName)
                .courseImageName(courseImageName)
                .courseDescription(courseDescription)
                .level(level)
                .maxPoints(maxPoints)
                .duration(duration)
                .isApproved(isApproved)
                .isRejected(isRejected)
                .category(category)
                .author(author.toResponse())
                .contents(checkedContents.stream().map(c -> c.toResponse()).toList())
                .createdAt(this.getCreatedAt())
                .editedAt(this.getEditedAt())
                .build();

    }
}
