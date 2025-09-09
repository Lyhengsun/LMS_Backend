package com.norton.lms_backend.model.dto.request;

import java.util.List;

import com.norton.lms_backend.model.entity.Question;
import com.norton.lms_backend.model.entity.Quiz;
import com.norton.lms_backend.model.enumeration.QuestionType;

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
public class QuestionRequest {
    private String content;
    private QuestionType questionType;
    private Boolean trueFalseAnswer;
    private Integer score;
    private List<AnswerRequest> answers;

    public Question toEntity(Quiz quiz) {
        return Question.builder()
                .content(content)
                .questionType(questionType)
                .trueFalseAnswer(trueFalseAnswer)
                .score(score)
                .quiz(quiz)
                .build();
    }
}
