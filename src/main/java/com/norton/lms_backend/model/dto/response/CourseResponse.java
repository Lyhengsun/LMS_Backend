package com.norton.lms_backend.model.dto.response;

import java.util.List;

import com.norton.lms_backend.model.entity.AppUser;
import com.norton.lms_backend.model.entity.Category;
import com.norton.lms_backend.model.enumeration.Level;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class CourseResponse extends BaseEntityResponse {
    private String courseName;

    private String courseImageName;

    private String courseDescription;

    private Level level;

    private Integer maxPoints;

    private Boolean isPublic;

    private Boolean isDeleted;

    private Category category;

    private AppUserResponse author;
}
