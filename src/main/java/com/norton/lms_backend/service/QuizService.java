package com.norton.lms_backend.service;

import java.util.List;

import com.norton.lms_backend.model.dto.request.AnswerRequest;
import com.norton.lms_backend.model.dto.request.QuestionRequest;
import com.norton.lms_backend.model.dto.request.QuizRequest;
import com.norton.lms_backend.model.dto.response.*;

import com.norton.lms_backend.model.enumeration.CourseLevel;
import com.norton.lms_backend.model.enumeration.QuizProperty;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Sort;

public interface QuizService {
    /**
     * Create a new quiz from QuizRequest data.
     *
     * @param quizRequest Request object containing quiz details.
     * @return Created Quiz entity.
     */
    QuizResponse createQuiz(QuizRequest quizRequest);

    /**
     * Get all quizzes.
     *
     * @return List of all Quiz entities.
     */
    PagedResponse<QuizNoQuestionResponse> getAllQuizzes(
            Integer page,
            Integer size,
            String name,
            Long categoryId,
            CourseLevel level,
            QuizProperty quizProperty,
            Sort.Direction direction
    );

    /**
     * Get a quiz by its ID.
     *
     * @param id Quiz ID.
     * @return Optional containing Quiz if found.
     */
    QuizResponse getQuizById(Long id);

    /**
     * Update a quiz by ID using QuizRequest data.
     *
     * @param id Quiz ID.
     * @param quizRequest Request object with updated details.
     * @return Updated Quiz entity.
     */
    QuizResponse updateQuiz(Long id, QuizRequest quizRequest);

    /**
     * Delete a quiz by its ID.
     *
     * @param id Quiz ID.
     */
    void deleteQuiz(Long id);

    PagedResponse<QuizNoQuestionResponse> getAllYourQuizzes(@Positive Integer page, @Positive Integer size);

    PagedResponse<QuizNoQuestionResponse> getAllQuizzesByAuthor(@Positive Integer page, @Positive Integer size, String name);

    QuizResponse getAllQuizzesByAuthorIdAndQuizId(Long authorId, Long quizId);

    QuestionResponse createQuestion(Long quizId, QuestionRequest request);

    AnswerResponse createAnswer(Long questionId, AnswerRequest request);

    AnswerStudentResponse chooseAnswer(Long takeQuizId, Long answerId);

    TakeQuizResponse studentTakeQuiz(Long quizId);

    void submitTakenQuiz(Long takeQuizId, List<Long> answerIds);

    TakeQuizResponse getTakenQuizById(Long takeQuizId);

    void studentDeleteTakeQuiz(Long takeQuizId);

    List<QuizResultResponse> getQuizResult(Long quizId);
}
