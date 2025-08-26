package com.norton.lms_backend.model.dto.response;

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
public class CourseContentResponse extends BaseEntityResponse {
    private String courseContentName;
    private Integer courseContentIndex;
    private String videoFileName;
    private Integer durationMinutes;
}
