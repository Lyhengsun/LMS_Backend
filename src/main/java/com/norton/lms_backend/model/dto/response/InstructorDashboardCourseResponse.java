package com.norton.lms_backend.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InstructorDashboardCourseResponse {
    private Long courseId;
    private String courseName;
    private String categoryName;
    private String status; // "Published", "Draft", "Pending Approval"
    private Integer enrollmentCount;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

