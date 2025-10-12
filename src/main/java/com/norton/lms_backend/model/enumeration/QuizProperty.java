package com.norton.lms_backend.model.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuizProperty {
    QUIZ_NAME("quizName"),
    CREATED_AT("createdAt");

    private final String value;
}
