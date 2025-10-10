package com.norton.lms_backend.controller;

import com.norton.lms_backend.model.dto.request.AnswerRequest;
import com.norton.lms_backend.model.dto.request.QuestionRequest;
import com.norton.lms_backend.model.dto.request.QuizRequest;
import com.norton.lms_backend.model.dto.response.*;
import com.norton.lms_backend.service.QuizService;
import com.norton.lms_backend.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1")
@SecurityRequirement(name = "bearerAuth")
public class QuizController {
    private final QuizService quizService;

    @GetMapping("/quizzes")
    public ResponseEntity<ApiResponse<PagedResponse<QuizNoQuestionResponse>>> getAllQuizzes(
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            @RequestParam(required = false) String name
            ) {
        return ResponseUtils.createResponse("Get all quizzes successfully", quizService.getAllQuizzes(page, size, name));
    }

    @GetMapping("/quizzes/{quiz-id}")
    public ResponseEntity<ApiResponse<QuizResponse>> getQuizById(@PathVariable("quiz-id") Long id) {
        return ResponseUtils.createResponse("Get quiz by id successfully", quizService.getQuizById(id));
    }

    @PostMapping("/instructors/quizzes")
    public ResponseEntity<ApiResponse<QuizResponse>> createQuiz(@RequestBody QuizRequest quizRequest) {
        return ResponseUtils.createResponse("Quiz created successfully", quizService.createQuiz(quizRequest));
    }

    @PutMapping("/instructors/quizzes/{quiz-id}")
    public ResponseEntity<ApiResponse<QuizResponse>> updateQuiz(@PathVariable("quiz-id") Long id,
            @RequestBody QuizRequest quizRequest) {
        return ResponseUtils.createResponse("Quiz updated successfully", quizService.updateQuiz(id, quizRequest));
    }

    @DeleteMapping("/instructors/quizzes/{quiz-id}")
    public ResponseEntity<ApiResponse<Object>> deleteQuiz(@PathVariable("quiz-id") Long id) {
        quizService.deleteQuiz(id);
        return ResponseUtils.createResponse("Delete quiz successfully");
    }

    @GetMapping("/instructors/quizzes")
    public ResponseEntity<ApiResponse<PagedResponse<QuizNoQuestionResponse>>> getAllQuizzesByAuthor(
            @RequestParam(defaultValue = "1") @Positive Integer page,
            @RequestParam(defaultValue = "10") @Positive Integer size,
            @RequestParam(required = false) String name) {
        return ResponseUtils.createResponse("Get all your quizzes successfully",
                quizService.getAllQuizzesByAuthor(page, size, name));
    }

    @PostMapping("/instructors/quizzes/{quizId}/questions")
    public ResponseEntity<ApiResponse<QuestionResponse>> createQuestion(@PathVariable Long quizId,
            @RequestBody QuestionRequest request) {
        return ResponseUtils.createResponse("Create question successfully", HttpStatus.CREATED,
                quizService.createQuestion(quizId, request));
    }

    @PostMapping("/instructors/quizzes/questions/{questionId}/answers")
    public ResponseEntity<ApiResponse<AnswerResponse>> createAnswer(@PathVariable Long questionId,
            @RequestBody AnswerRequest request) {
        return ResponseUtils.createResponse("Create new answer successfully", HttpStatus.CREATED,
                quizService.createAnswer(questionId, request));
    }

    // @PostMapping("/students/quizzes/taken/{takeQuizId}/answers/{answerId}/chosen")
    public ResponseEntity<ApiResponse<AnswerStudentResponse>> chooseAnswer(@PathVariable Long takeQuizId,
            @PathVariable Long answerId) {
        return ResponseUtils.createResponse("Choose an answer successfully", HttpStatus.OK,
                quizService.chooseAnswer(takeQuizId, answerId));
    }

    @PostMapping("/students/quizzes/{quizId}/taken")
    public ResponseEntity<ApiResponse<TakeQuizResponse>> takeQuiz(@PathVariable("quizId") Long quizId) {
        return ResponseUtils.createResponse("Take quiz with id: " + quizId + " successfully", HttpStatus.OK,
                quizService.studentTakeQuiz(quizId));
    }

    @DeleteMapping("/students/quizzes/taken/{takeQuizId}")
    public ResponseEntity<ApiResponse<Void>> deleteTakeQuiz(@PathVariable("takeQuizId") Long takeQuizId) {
        quizService.studentDeleteTakeQuiz(takeQuizId);
        return ResponseUtils.createResponse("Quiz session with id: " + takeQuizId + " deleted successfully");
    }

    @GetMapping("/students/quizzes/taken/{takeQuizId}")
    public ResponseEntity<ApiResponse<TakeQuizResponse>> getTakenQuizById(@PathVariable Long takeQuizId) {
        return ResponseUtils.createResponse("Fetch taken quiz successfully", quizService.getTakenQuizById(takeQuizId));
    }

    @PostMapping("/students/quizzes/taken/{takeQuizId}/submit")
    public ResponseEntity<ApiResponse<Void>> submitTakenQuiz(@PathVariable Long takeQuizId, @RequestParam List<Long> answerIds) {
        quizService.submitTakenQuiz(takeQuizId, answerIds);

        return ResponseUtils.createResponse("Submit quiz successfully");
    }

    @GetMapping("/students/quizzes/{quizId}/result")
    public ResponseEntity<ApiResponse<List<QuizResultResponse>>> getQuizResult(@PathVariable Long quizId) {
        List<QuizResultResponse> quizResults = quizService.getQuizResult(quizId);
        return ResponseUtils.createResponse("fetch user result successfully", quizResults);
    }

    // @GetMapping("/instructors")
    // public ResponseEntity<ApiResponse<PagedResponse<QuizResponse>>>
    // getAllQuizzesByAuthorId(
    // @PathVariable("author-id") Long id,
    // @RequestParam(defaultValue = "1") @Positive Integer page,
    // @RequestParam(defaultValue = "10") @Positive Integer size) {
    // return ResponseUtils.createResponse("Get all quizzes of author successfully",
    // quizService.getAllQuizzesByAuthorId(id, page, size));
    // }

    // @GetMapping("/author/{author-id}/{quiz-id}")
    // public ResponseEntity<ApiResponse<QuizResponse>>
    // getAllQuizzesByAuthorIdAndQuizId(
    // @PathVariable("author-id") Long authorId,
    // @PathVariable("quiz-id") Long quizId) {
    // return ResponseUtils.createResponse("Get all quizzes of author successfully",
    // quizService.getAllQuizzesByAuthorIdAndQuizId(authorId, quizId));
    // }
}
