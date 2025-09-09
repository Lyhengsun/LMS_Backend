package com.norton.lms_backend.model.dto.request;

import com.norton.lms_backend.model.entity.Course;
import com.norton.lms_backend.model.entity.CourseDraft;
import com.norton.lms_backend.model.enumeration.CourseLevel;
import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRequest {

    @NotBlank(message = "Course name is required")
    @Size(max = 50, message = "Course name must not exceed 50 characters")
    private String courseName;

    @NotBlank(message = "Course Image is required")
    private String courseImageName;

    @NotBlank(message = "Course description is required")
    private String courseDescription;

    @NotNull(message = "Level is required")
    private CourseLevel level;

    @NotNull(message = "Max points is required")
    @Min(value = 1, message = "Max points must be greater than 0")
    private Integer maxPoints;

    @NotNull(message = "Course category ID is required")
    private Long courseCategoryId;

    public Course toEntity() {
        return Course.builder()
                .courseName(this.courseName)
                .courseImageName(courseImageName)
                .courseDescription(this.courseDescription)
                .level(this.level)
                .maxPoints(this.maxPoints)
                .build();
    }

    public CourseDraft toEntityDraft() {
        return CourseDraft.builder()
                .courseName(courseName)
                .courseImageName(courseImageName)
                .courseDescription(courseDescription)
                .level(level)
                .maxPoints(maxPoints)
                .build();
    }
}
