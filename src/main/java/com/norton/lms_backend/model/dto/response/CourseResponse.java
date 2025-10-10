package com.norton.lms_backend.model.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CourseResponse extends BaseEntityResponse {
    private String courseName;

    private String courseImageName;

    private String courseDescription;

    private CourseLevel level;

    private Integer maxPoints;

    private Boolean isPublic;

    private Boolean isDeleted;

    private Integer duration;

    private Category category;

    private Integer studentEnrolled;

    private AppUserResponse author;

    private List<CourseContentResponse> contents;
}
