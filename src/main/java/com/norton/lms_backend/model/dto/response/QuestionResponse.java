package com.norton.lms_backend.model.dto.response;

import java.util.List;

import com.norton.lms_backend.model.enumeration.QuestionType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class QuestionResponse extends BaseEntityResponse {
    private String content;
    private QuestionType questionType;
    private Boolean trueFalseAnswer;
    private Integer score;
    private List<AnswerResponse> answers;
}
