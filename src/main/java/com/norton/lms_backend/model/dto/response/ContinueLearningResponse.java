package com.norton.lms_backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContinueLearningResponse {
    private Long courseId;
    private String courseName;
    private String categoryName;
    private Integer progress;
    private String thumbnailUrl;
    private Integer timeRemainingInMinutes;
    private CourseContentProgressResponse nextContent;
}
