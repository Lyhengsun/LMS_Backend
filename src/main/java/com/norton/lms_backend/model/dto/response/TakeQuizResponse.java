package com.norton.lms_backend.model.dto.response;

import java.time.LocalDateTime;

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
public class TakeQuizResponse extends BaseEntityResponse {
    private AppUserResponse user;
    private QuizNoQuestionResponse quiz;
    private LocalDateTime deadlineTime;
}
