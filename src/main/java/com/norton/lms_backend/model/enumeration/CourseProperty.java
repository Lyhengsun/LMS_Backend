package com.norton.lms_backend.model.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CourseProperty {
    COURSE_NAME("courseName"), CREATED_AT("createdAt");

    private final String value;
}
