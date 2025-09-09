package com.norton.lms_backend.model.dto.request;

import com.norton.lms_backend.model.entity.Answer;
import com.norton.lms_backend.model.entity.Question;

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
public class AnswerRequest {
    private String content;
    private Boolean isCorrect;

    public Answer toEntity(Question question) {
        return Answer.builder()
                .content(content)
                .isCorrect(isCorrect)
                .question(question)
                .build();
    }
}
