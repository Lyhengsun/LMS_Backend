package com.norton.lms_backend.model.dto.response;

import com.norton.lms_backend.model.entity.Category;
import com.norton.lms_backend.model.enumeration.CourseAvailability;
import com.norton.lms_backend.model.enumeration.CourseLevel;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CourseNoContentResponse extends BaseEntityResponse {
    private String courseName;

    private String courseImageName;

    private String courseDescription;

    private CourseLevel level;

    private Integer maxPoints;

    private Boolean isPublic;

    private Boolean isDeleted;

    private Integer duration;

    private Category category;

    private AppUserResponse author;

    private Integer contentCount;

    private CourseAvailability courseAvailability;

    private Double price;

    @Builder.Default
    private Boolean isAccessible = true;
}
