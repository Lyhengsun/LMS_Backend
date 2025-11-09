package com.norton.lms_backend.model.dto.request;

import com.norton.lms_backend.model.entity.CourseContent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseContentRequest {
    private String courseContentName;
    private Integer courseContentIndex;
    private String videoFileName;
    private Integer durationMinutes;
    private Integer points;
    private Boolean requirePayment;
    private Long courseId;
    private Long courseDraftId;

    public CourseContent toEntity() {
        return CourseContent.builder()
                .courseContentName(courseContentName)
                .courseContentIndex(courseContentIndex)
                .videoFileName(videoFileName)
                .durationMinutes(durationMinutes)
                .points(points)
                .requirePayment(requirePayment)
                .build();
    }
}
