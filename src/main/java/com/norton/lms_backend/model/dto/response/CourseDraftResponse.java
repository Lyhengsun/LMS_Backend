package com.norton.lms_backend.model.dto.response;

import java.util.List;

import com.norton.lms_backend.model.entity.Category;
import com.norton.lms_backend.model.enumeration.CourseLevel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CourseDraftResponse extends BaseEntityResponse {
    private String courseName;

    private String courseImageName;

    private String courseDescription;

    private CourseLevel level;

    private Integer maxPoints;

    private Boolean isApproved;

    private Boolean isRejected;

    private Integer duration;

    private Category category;

    private AppUserResponse author;

    private List<CourseContentResponse> contents;
}
