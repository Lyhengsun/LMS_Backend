package com.norton.lms_backend.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import com.norton.lms_backend.model.dto.response.AnswerResponse;
import com.norton.lms_backend.model.dto.response.AnswerStudentResponse;
import com.norton.lms_backend.model.dto.response.QuestionResponse;
import com.norton.lms_backend.model.dto.response.QuestionStudentResponse;
import com.norton.lms_backend.model.enumeration.QuestionType;

@Getter
@Setter
@Entity
@Table(name = "questions")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Question extends BaseEntity {

    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;

    @Column(name = "true_false_answer", nullable = true)
    private Boolean trueFalseAnswer;

    @Column(name = "score", nullable = false)
    private Integer score;

    @ManyToOne
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    private List<Answer> answers;

    public QuestionResponse toResponse() {
        List<AnswerResponse> answerResponses = null;
        if (answers != null && answers.size() > 0) {
            answerResponses = answers.stream().map(a -> a.toResponse()).toList();
        }

        return QuestionResponse.builder()
                .content(content)
                .questionType(questionType)
                .trueFalseAnswer(trueFalseAnswer)
                .score(score)
                .answers(answerResponses)
                .createdAt(getCreatedAt())
                .editedAt(getEditedAt())
                .build();
    }

    public QuestionStudentResponse toStudentResponse() {
        List<AnswerStudentResponse> answerResponses = null;
        if (answers != null && answers.size() > 0) {
            answerResponses = answers.stream().map(a -> a.toStudentResponse()).toList();
        }

        return QuestionStudentResponse.builder()
                .content(content)
                .questionType(questionType)
                .trueFalseAnswer(trueFalseAnswer)
                .score(score)
                .answers(answerResponses)
                .createdAt(getCreatedAt())
                .editedAt(getEditedAt())
                .build();
    }
}
