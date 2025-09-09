package com.norton.lms_backend.service;

import com.norton.lms_backend.model.dto.request.AnswerRequest;
import com.norton.lms_backend.model.dto.request.QuestionRequest;
import com.norton.lms_backend.model.dto.request.QuizRequest;
import com.norton.lms_backend.model.dto.response.AnswerResponse;
import com.norton.lms_backend.model.dto.response.AnswerStudentResponse;
import com.norton.lms_backend.model.dto.response.PagedResponse;
import com.norton.lms_backend.model.dto.response.QuestionResponse;
import com.norton.lms_backend.model.dto.response.QuizResponse;
import com.norton.lms_backend.model.dto.response.QuizStudentResponse;
import com.norton.lms_backend.model.dto.response.TakeQuizResponse;

import jakarta.validation.constraints.Positive;

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
    PagedResponse<QuizResponse> getAllQuizzes(Integer page, Integer size);

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

    PagedResponse<QuizResponse> getAllYourQuizzes(@Positive Integer page, @Positive Integer size);

    PagedResponse<QuizResponse> getAllQuizzesByAuthorId(Long id, @Positive Integer page, @Positive Integer size);

    QuizResponse getAllQuizzesByAuthorIdAndQuizId(Long authorId, Long quizId);

    QuestionResponse createQuestion(Long quizId, QuestionRequest request);

    AnswerResponse createAnswer(Long questionId, AnswerRequest request);

    AnswerStudentResponse chooseAnswer(Long takeQuizId, Long answerId);

    TakeQuizResponse studentTakeQuiz(Long quizId);

    void submitTakenQuiz(Long takeQuizId);
}
