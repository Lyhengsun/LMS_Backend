package com.norton.lms_backend.model.entity;

import java.util.List;

import com.norton.lms_backend.model.dto.response.AnswerResponse;
import com.norton.lms_backend.model.dto.response.AnswerStudentResponse;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "answers")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Answer extends BaseEntity {

    @Column(name = "content", nullable = false, columnDefinition = "text")
    private String content;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL)
    private List<UserAnswer> userAnswers;

    public AnswerResponse toResponse() {
        return AnswerResponse.builder()
                .id(getId())
                .content(content)
                .isCorrect(isCorrect)
                .createdAt(getCreatedAt())
                .editedAt(getEditedAt())
                .build();
    }

    public AnswerStudentResponse toStudentResponse() {
        return AnswerStudentResponse.builder()
                .id(getId())
                .content(content)
                .createdAt(getCreatedAt())
                .editedAt(getEditedAt())
                .build();
    }
}
