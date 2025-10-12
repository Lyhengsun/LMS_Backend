package com.norton.lms_backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseStatusDistributionResponse {
    private Integer published;
    private Integer draft;
    private Integer pending;
    private Integer rejected;
}
