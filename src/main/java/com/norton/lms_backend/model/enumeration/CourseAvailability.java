package com.norton.lms_backend.model.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CourseAvailability {
    FREE("free"), PAID("paid"), PARTIAL("partial");

    private final String value;
}
