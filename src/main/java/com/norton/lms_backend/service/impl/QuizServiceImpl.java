package com.norton.lms_backend.service.impl;

import com.norton.lms_backend.exception.BadRequestException;
import com.norton.lms_backend.exception.NotFoundException;
import com.norton.lms_backend.model.dto.request.QuizRequest;
import com.norton.lms_backend.model.dto.response.PagedResponse;
import com.norton.lms_backend.model.dto.response.PaginationInfo;
import com.norton.lms_backend.model.dto.response.QuizResponse;
import com.norton.lms_backend.model.entity.AppUser;
import com.norton.lms_backend.model.entity.Category;
import com.norton.lms_backend.model.entity.Quiz;
import com.norton.lms_backend.repository.QuizRepository;
import com.norton.lms_backend.service.CategoryService;
import com.norton.lms_backend.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final CategoryService categoryService;

    private AppUser getCurrentUser() {
        return (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public Quiz findQuizById(Long id) {
        return quizRepository.findById(id).orElseThrow(() -> new NotFoundException("Quiz of id " + id + " not found"));
    }

    @Override
    public QuizResponse createQuiz(QuizRequest quizRequest) {
        Quiz quiz = quizRequest.toEntity();
        quiz.setAuthor(getCurrentUser());
        Category category = categoryService.getCategory(quizRequest.getCategoryId());
        quiz.setCategory(category);
        return quizRepository.save(quiz).toResponse();
    }

    @Override
    public PagedResponse<QuizResponse> getAllQuizzes(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Quiz> result = quizRepository.findAll(pageable);
        return PagedResponse.<QuizResponse>builder()
                .items(result.getContent().stream().map(Quiz::toResponse).toList())
                .pagination(new PaginationInfo(result))
                .build();
    }

    @Override
    public QuizResponse getQuizById(Long id) {
        return quizRepository.findById(id).orElseThrow(() -> new NotFoundException("Quiz of id " + id + " not found")).toResponse();
    }

    @Override
    public QuizResponse updateQuiz(Long id, QuizRequest quizRequest) {
        Quiz quiz = findQuizById(id); // existing entity from DB

        // Update fields manually
        quiz.setQuizName(quizRequest.getQuizName());
        quiz.setQuizDescription(quizRequest.getQuizDescription());
        quiz.setQuizInstruction(quizRequest.getQuizInstruction());
        quiz.setLevel(quizRequest.getLevel());
        quiz.setDurationMinutes(quizRequest.getDurationMinutes());
        quiz.setMaxAttempts(quizRequest.getMaxAttempts());
        quiz.setPassingScore(quizRequest.getPassingScore());

        // Set category
        Category category = categoryService.getCategory(quizRequest.getCategoryId());
        quiz.setCategory(category);

        // Save updated entity
        return quizRepository.save(quiz).toResponse();
    }

    @Override
    public void deleteQuiz(Long id) {
        Quiz quiz = findQuizById(id);
        if (!getCurrentUser().getId().equals(quiz.getAuthor().getId())
                || !getCurrentUser().getRole().getId().equals(1))
            throw new BadRequestException("Not owner or admin");
        quizRepository.delete(quiz);
    }

    @Override
    public PagedResponse<QuizResponse> getAllYourQuizzes(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Quiz> result = quizRepository.findAllByAuthorId(getCurrentUser().getId(), pageable);
        return PagedResponse.<QuizResponse>builder()
                .items(result.getContent().stream().map(Quiz::toResponse).toList())
                .pagination(new PaginationInfo(result))
                .build();
    }

    @Override
    public PagedResponse<QuizResponse> getAllQuizzesByAuthorId(Long id, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page-1, size);
        Page<Quiz> result = quizRepository.findAllByAuthorId(id, pageable);
        return PagedResponse.<QuizResponse>builder()
                .items(result.getContent().stream().map(Quiz::toResponse).toList())
                .pagination(new PaginationInfo(result))
                .build();
    }

    @Override
    public QuizResponse getAllQuizzesByAuthorIdAndQuizId(Long authorId, Long quizId) {
        return quizRepository.findByAuthorIdAndId(authorId, quizId).toResponse();
    }
}
