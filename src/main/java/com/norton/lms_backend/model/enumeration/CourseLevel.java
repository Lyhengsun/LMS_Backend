package com.norton.lms_backend.model.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CourseLevel {
    BEGINNER("beginner"), INTERMEDIATE("intermediate"), ADVANCE("advance");

    private final String fieldName;
}
