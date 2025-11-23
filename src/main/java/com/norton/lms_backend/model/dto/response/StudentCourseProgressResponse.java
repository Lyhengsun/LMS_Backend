package com.norton.lms_backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StudentCourseProgressResponse {
    private Long id;
    private String fullName;
    private String email;
    private Integer progressInPercentage;
    private LocalDate enrolledDate;
}
