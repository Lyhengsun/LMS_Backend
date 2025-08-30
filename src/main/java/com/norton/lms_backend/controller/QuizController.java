package com.norton.lms_backend.controller;

import com.norton.lms_backend.model.dto.request.QuizRequest;
import com.norton.lms_backend.model.dto.response.ApiResponse;
import com.norton.lms_backend.model.dto.response.PagedResponse;
import com.norton.lms_backend.model.dto.response.QuizResponse;
import com.norton.lms_backend.service.QuizService;
import com.norton.lms_backend.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/quizzes")
@SecurityRequirement(name = "bearerAuth")
public class QuizController {
    private final QuizService quizService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<QuizResponse>>> getAllQuizzes( @RequestParam(defaultValue = "1") @Positive Integer page,
                                                                                   @RequestParam(defaultValue = "10") @Positive Integer size) {
        return ResponseUtils.createResponse("Get all quizzes successfully", quizService.getAllQuizzes(page, size));
    }

    @GetMapping("{quiz-id}")
    public ResponseEntity<ApiResponse<QuizResponse>> getQuizById(@PathVariable("quiz-id") Long id) {
        return ResponseUtils.createResponse("Get quiz by id successfully", quizService.getQuizById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<QuizResponse>> createQuiz(@RequestBody QuizRequest quizRequest) {
        return ResponseUtils.createResponse("Quiz created successfully", quizService.createQuiz(quizRequest));
    }

    @PutMapping("{quiz-id}")
    public ResponseEntity<ApiResponse<QuizResponse>> updateQuiz(@PathVariable("quiz-id") Long id, @RequestBody QuizRequest quizRequest) {
        return ResponseUtils.createResponse("Quiz updated successfully", quizService.updateQuiz(id, quizRequest));
    }

    @DeleteMapping("{quiz-id}")
    public ResponseEntity<ApiResponse<Object>> deleteQuiz(@PathVariable("quiz-id") Long id) {
        quizService.deleteQuiz(id);
        return ResponseUtils.createResponse("Delete quiz successfully");
    }

    @GetMapping("/author/current-user")
    public ResponseEntity<ApiResponse<PagedResponse<QuizResponse>>> getAllYourQuizzes( @RequestParam(defaultValue = "1") @Positive Integer page,
                                                                                   @RequestParam(defaultValue = "10") @Positive Integer size) {
        return ResponseUtils.createResponse("Get all your quizzes successfully", quizService.getAllYourQuizzes(page, size));
    }

    @GetMapping("/author/{author-id}")
    public ResponseEntity<ApiResponse<PagedResponse<QuizResponse>>> getAllQuizzesByAuthorId(@PathVariable("author-id") Long id,
                                                                                            @RequestParam(defaultValue = "1") @Positive Integer page,
                                                                                       @RequestParam(defaultValue = "10") @Positive Integer size) {
        return ResponseUtils.createResponse("Get all quizzes of author successfully", quizService.getAllQuizzesByAuthorId(id, page, size));
    }

    @GetMapping("/author/{author-id}/{quiz-id}")
    public ResponseEntity<ApiResponse<QuizResponse>> getAllQuizzesByAuthorIdAndQuizId(@PathVariable("author-id") Long authorId,
                                                                                                     @PathVariable("quiz-id") Long quizId) {
        return ResponseUtils.createResponse("Get all quizzes of author successfully", quizService.getAllQuizzesByAuthorIdAndQuizId(authorId, quizId));
    }
}
