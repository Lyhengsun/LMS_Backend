package com.norton.lms_backend.model.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UserProperty {
    ID("id"), FULLNAME("fullName"), EMAIL("email");
    private final String value;
}
